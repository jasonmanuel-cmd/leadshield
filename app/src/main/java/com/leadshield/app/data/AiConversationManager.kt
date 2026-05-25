package com.leadshield.app.data

import com.google.ai.client.generativeai.GenerativeModel
import com.leadshield.app.BuildConfig
import kotlinx.coroutines.flow.first
import org.json.JSONObject

/**
 * Core engine for AI-driven conversation continuation (Operator tier feature).
 *
 * When a caller texts back within 4 hours of the initial missed-call auto-reply,
 * this manager picks up the thread, builds a Gemini prompt from the business profile
 * and conversation history, and returns the AI-generated reply to send.
 *
 * Exchange cap: after 4 AI replies, a human handoff message is sent and the
 * conversation is closed.
 */

data class AiReplyResult(
    val replyText: String,
    val isUrgent: Boolean,
    val extractedName: String? = null,
    val extractedNeed: String? = null,
    val extractedCity: String? = null
)

class AiConversationManager(
    private val prefs: AppPreferences,
    private val db: AppDatabase
) {

    companion object {
        private const val MAX_EXCHANGES = 4
        private const val CONVERSATION_TTL_MS = 4 * 60 * 60 * 1000L // 4 hours

        private val URGENCY_KEYWORDS = listOf(
            "urgent", "emergency", "asap", "flooding", "leaking",
            "no heat", "broken", "burst", "help"
        )
    }

    /**
     * Handle an incoming SMS reply from [phoneNumber] with body [incomingMessage].
     *
     * @return [AiReplyResult] with reply text and metadata, or null if no AI reply should be sent.
     */
    suspend fun handleIncomingReply(phoneNumber: String, incomingMessage: String): AiReplyResult? {
        // 1. Tier check
        val tier = prefs.getSubscriptionTierSync()
        if (!tier.hasAiConversation) return null

        // 2. Feature toggle check
        val aiEnabled = prefs.aiConversationEnabled.first()
        if (!aiEnabled) return null

        // 3. Active conversation check
        val dao = db.conversationDao()
        val conversation = dao.getConversation(phoneNumber)
        val now = System.currentTimeMillis()

        if (conversation == null || !conversation.isActive) return null
        if (now - conversation.startedAt > CONVERSATION_TTL_MS) {
            dao.closeConversation(phoneNumber)
            return null
        }

        // 4. Exchange cap — send handoff and close
        val profile = prefs.getBusinessProfileSync()
        if (conversation.exchangeCount >= MAX_EXCHANGES) {
            dao.closeConversation(phoneNumber)
            return AiReplyResult(
                replyText = buildHandoffMessage(profile),
                isUrgent = false
            )
        }

        // 5. Urgency detection
        val lowerMessage = incomingMessage.lowercase()
        val isUrgent = URGENCY_KEYWORDS.any { keyword -> lowerMessage.contains(keyword) }

        // 6. Store the incoming user message
        dao.insertMessage(
            ConversationMessageEntity(
                phoneNumber = phoneNumber,
                role = "user",
                content = incomingMessage,
                timestamp = now
            )
        )

        // 7. Build Gemini prompt
        val history = dao.getMessagesForNumber(phoneNumber)
        val prompt = buildPrompt(profile, history, isUrgent)

        // 8. Generate reply via Gemini
        val rawResponse = try {
            val model = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = BuildConfig.GEMINI_API_KEY
            )
            val response = model.generateContent(prompt)
            response.text?.trim() ?: return null
        } catch (e: Exception) {
            return null
        }

        // 9. Parse JSON response
        val (replyText, extractedName, extractedNeed, extractedCity) = parseGeminiResponse(rawResponse)
        if (replyText.isBlank()) return null

        // 10. Store the AI reply
        dao.insertMessage(
            ConversationMessageEntity(
                phoneNumber = phoneNumber,
                role = "assistant",
                content = replyText,
                timestamp = System.currentTimeMillis()
            )
        )

        // 11. Increment exchange count and update lastMessageAt
        dao.upsertConversation(
            conversation.copy(
                exchangeCount = conversation.exchangeCount + 1,
                lastMessageAt = System.currentTimeMillis()
            )
        )

        // 12. Upsert LeadCard with extracted info
        val leadDao = db.leadCardDao()
        val existingLead = leadDao.getByPhone(phoneNumber)
        val urgencyLevel = if (isUrgent) "urgent" else "normal"
        val leadScore = LeadScoringEngine.scoreMessage(incomingMessage)
        if (existingLead == null) {
            leadDao.upsert(
                LeadCardEntity(
                    phoneNumber = phoneNumber,
                    contactName = extractedName,
                    serviceNeeded = extractedNeed,
                    city = extractedCity,
                    urgencyLevel = urgencyLevel,
                    status = "new",
                    leadScore = leadScore,
                    createdAt = now,
                    updatedAt = now
                )
            )
        } else {
            // Update fields if we extracted new data; only upgrade score, never downgrade
            val newScore = maxOf(existingLead.leadScore, leadScore)
            leadDao.upsert(
                existingLead.copy(
                    contactName = extractedName ?: existingLead.contactName,
                    serviceNeeded = extractedNeed ?: existingLead.serviceNeeded,
                    city = extractedCity ?: existingLead.city,
                    urgencyLevel = if (isUrgent) "urgent" else existingLead.urgencyLevel,
                    leadScore = newScore,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
        // Also update leadScore via dedicated query to ensure consistency
        leadDao.updateLeadScore(phoneNumber, maxOf(existingLead?.leadScore ?: 0, leadScore))

        return AiReplyResult(
            replyText = replyText,
            isUrgent = isUrgent,
            extractedName = extractedName,
            extractedNeed = extractedNeed,
            extractedCity = extractedCity
        )
    }

    private data class ParsedResponse(
        val reply: String,
        val name: String?,
        val need: String?,
        val city: String?
    )

    private fun parseGeminiResponse(raw: String): ParsedResponse {
        // Try to parse JSON block first
        return try {
            val jsonStart = raw.indexOf('{')
            val jsonEnd = raw.lastIndexOf('}')
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                val json = JSONObject(raw.substring(jsonStart, jsonEnd + 1))
                ParsedResponse(
                    reply = json.optString("reply", raw.substringBefore("{").trim().ifBlank { raw }),
                    name = json.optString("name").takeIf { it.isNotBlank() },
                    need = json.optString("need").takeIf { it.isNotBlank() },
                    city = json.optString("city").takeIf { it.isNotBlank() }
                )
            } else {
                ParsedResponse(reply = raw, name = null, need = null, city = null)
            }
        } catch (e: Exception) {
            ParsedResponse(reply = raw, name = null, need = null, city = null)
        }
    }

    private fun buildPrompt(
        profile: BusinessProfile,
        history: List<ConversationMessageEntity>,
        isUrgent: Boolean
    ): String {
        val systemPrompt = buildString {
            if (isUrgent) {
                append("URGENT: The caller has indicated an emergency. Lead with empathy and set an aggressive callback time.\n\n")
            }
            append("You are an AI receptionist texting on behalf of ${profile.businessName}, a ${profile.tradeType} business in ${profile.serviceArea}.\n")
            append("Business owner: ${profile.ownerName}\n")
            append("Callback timeframe: ${profile.callbackWindow}\n")
            if (profile.pricingInfo.isNotBlank()) {
                append("Pricing: ${profile.pricingInfo}\n")
            }
            append("\n")
            append("You're responding via SMS to someone who called and missed. Be professional, friendly, and BRIEF (aim for under 160 characters).\n")
            append("Your goals: answer their question if you can, capture their name and need, set a callback expectation.\n")
            append("After 3-4 exchanges, offer to have ${profile.ownerName} call them back.\n")
            append("Never make up prices you don't know. Never promise things you can't deliver.\n")
            append("\n")
            append("IMPORTANT: Respond with a JSON object containing:\n")
            append("- \"reply\": your SMS reply text (under 160 chars)\n")
            append("- \"name\": caller's first name if you can identify it (or empty string)\n")
            append("- \"need\": the service they need in 3-5 words (or empty string)\n")
            append("- \"city\": their city/area if mentioned (or empty string)\n")
            append("\n")
            append("Conversation so far:\n")
        }

        val conversationText = buildString {
            for (msg in history) {
                val label = if (msg.role == "assistant") "You" else "Caller"
                append("$label: ${msg.content}\n")
            }
        }

        return "$systemPrompt$conversationText\nRespond with JSON only:"
    }

    private fun buildHandoffMessage(profile: BusinessProfile): String {
        return "Thanks for your patience! I'll have ${profile.ownerName} from ${profile.businessName} call you back ${profile.callbackWindow}. Talk soon!"
    }

    /**
     * Opens a new AI conversation for [phoneNumber], storing the initial assistant message
     * (the missed-call auto-reply) so the history is complete from the start.
     */
    suspend fun startConversation(phoneNumber: String, initialAssistantMessage: String) {
        val dao = db.conversationDao()
        val now = System.currentTimeMillis()
        dao.upsertConversation(
            ConversationEntity(
                phoneNumber = phoneNumber,
                startedAt = now,
                lastMessageAt = now,
                isActive = true,
                exchangeCount = 0
            )
        )
        dao.insertMessage(
            ConversationMessageEntity(
                phoneNumber = phoneNumber,
                role = "assistant",
                content = initialAssistantMessage,
                timestamp = now
            )
        )
    }
}
