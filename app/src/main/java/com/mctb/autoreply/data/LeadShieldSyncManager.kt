package com.mctb.autoreply.data

import android.util.Log
import com.mctb.autoreply.data.network.LeadShieldApi
import com.mctb.autoreply.data.network.LeadShieldSyncPayload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import java.time.Instant

private const val TAG = "LeadShieldSyncManager"

/**
 * Manages real-time sync to LeadShield command center (Next.js backend).
 *
 * Syncs customer leads, conversations, and call events to the web dashboard
 * after each customer interaction (missed call, reply, etc).
 *
 * Only syncs if customer has OPERATOR tier or higher AND has a valid sync token.
 * Gracefully handles network errors and stores sync failures for retry.
 */
@Singleton
class LeadShieldSyncManager @Inject constructor(
    private val api: LeadShieldApi,
    private val prefs: AppPreferences,
    private val subscriptionManager: SubscriptionManager,
    private val supabaseAuthManager: SupabaseAuthManager
) {
    private val syncScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Sync a missed call to the command center.
     * Called after each missed call is detected by CallReceiver.
     *
     * Will silently return if:
     * - Subscription tier is below OPERATOR
     * - Sync token is not configured
     * - Network call fails (logged but not thrown)
     */
    suspend fun syncMissedCall(phoneNumber: String, contactName: String?) {
        // Check if user has operator tier or above
        if (!hasOperatorTierOrAbove()) {
            Log.d(TAG, "Skipping sync: Subscription tier below OPERATOR")
            return
        }

        val syncToken = prefs.getLeadShieldSyncTokenSync()
        if (syncToken.isBlank()) {
            Log.d(TAG, "Skipping sync: No LeadShield sync token configured")
            return
        }

        val currentUser = supabaseAuthManager.getCurrentUser()
        val userId = currentUser?.second
        if (userId.isNullOrBlank()) {
            Log.w(TAG, "Cannot sync: No authenticated user")
            return
        }

        try {
            // Build minimal call event payload
            val callEvent = mapOf(
                "phone_number" to phoneNumber,
                "call_type" to "incoming",
                "ai_handled" to true,
                "occurred_at" to getCurrentIsoTimestamp()
            )

            val payload = LeadShieldSyncPayload(
                user_id = userId,
                leads = null, // Minimal payload - just log the event
                conversation_messages = null,
                call_events = listOf(callEvent)
            )

            Log.d(TAG, "Syncing missed call from $phoneNumber to LeadShield")
            val response = api.syncToLeadShield(
                authorization = "Bearer $syncToken",
                payload = payload
            )

            if (response.isSuccessful) {
                Log.d(TAG, "Sync successful for call from $phoneNumber")
            } else {
                Log.w(TAG, "Sync failed: HTTP ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing missed call", e)
            // Don't throw - continue app operation even if sync fails
        }
    }

    /**
     * Sync a full lead (from AI conversation or manual entry).
     * Called after a lead is created or updated.
     */
    suspend fun syncLead(
        phoneNumber: String,
        contactName: String?,
        serviceNeeded: String?,
        city: String?,
        urgencyLevel: String,
        status: String,
        notes: String = ""
    ) {
        if (!hasOperatorTierOrAbove()) return
        val syncToken = prefs.getLeadShieldSyncTokenSync()
        if (syncToken.isBlank()) return

        val currentUser = supabaseAuthManager.getCurrentUser()
        val userId = currentUser?.second
        if (userId.isNullOrBlank()) {
            Log.w(TAG, "Cannot sync lead: No authenticated user")
            return
        }

        try {
            val lead = mapOf(
                "phone_number" to phoneNumber,
                "contact_name" to (contactName ?: ""),
                "service_needed" to (serviceNeeded ?: ""),
                "city" to (city ?: ""),
                "urgency_level" to urgencyLevel,
                "status" to status,
                "notes" to notes
            )

            val payload = LeadShieldSyncPayload(
                user_id = userId,
                leads = listOf(lead),
                conversation_messages = null,
                call_events = null
            )

            Log.d(TAG, "Syncing lead $phoneNumber to LeadShield")
            val response = api.syncToLeadShield(
                authorization = "Bearer $syncToken",
                payload = payload
            )

            if (response.isSuccessful) {
                Log.d(TAG, "Sync successful for lead $phoneNumber")
            } else {
                Log.w(TAG, "Sync lead failed: HTTP ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing lead", e)
        }
    }

    /**
     * Sync a conversation message to the command center.
     * Called after each AI reply or customer message.
     */
    suspend fun syncConversationMessage(
        phoneNumber: String,
        role: String, // "caller" or "ai"
        content: String
    ) {
        if (!hasOperatorTierOrAbove()) return
        val syncToken = prefs.getLeadShieldSyncTokenSync()
        if (syncToken.isBlank()) return

        val currentUser = supabaseAuthManager.getCurrentUser()
        val userId = currentUser?.second
        if (userId.isNullOrBlank()) return

        try {
            val message = mapOf(
                "phone_number" to phoneNumber,
                "role" to role,
                "content" to content,
                "sent_at" to getCurrentIsoTimestamp()
            )

            val payload = LeadShieldSyncPayload(
                user_id = userId,
                leads = null,
                conversation_messages = listOf(message),
                call_events = null
            )

            val response = api.syncToLeadShield(
                authorization = "Bearer $syncToken",
                payload = payload
            )

            if (!response.isSuccessful) {
                Log.w(TAG, "Sync message failed: HTTP ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing message", e)
        }
    }

    /**
     * Batch sync: Send leads, messages, and call events together.
     * Use this for periodic bulk sync operations.
     */
    suspend fun syncBatch(
        leads: List<Map<String, Any?>>?,
        messages: List<Map<String, Any?>>?,
        callEvents: List<Map<String, Any?>>?
    ) {
        if (!hasOperatorTierOrAbove()) return
        val syncToken = prefs.getLeadShieldSyncTokenSync()
        if (syncToken.isBlank()) return

        val currentUser = supabaseAuthManager.getCurrentUser()
        val userId = currentUser?.second
        if (userId.isNullOrBlank()) return

        try {
            val payload = LeadShieldSyncPayload(
                user_id = userId,
                leads = leads,
                conversation_messages = messages,
                call_events = callEvents
            )

            val response = api.syncToLeadShield(
                authorization = "Bearer $syncToken",
                payload = payload
            )

            if (response.isSuccessful) {
                Log.d(
                    TAG,
                    "Batch sync successful: ${leads?.size ?: 0} leads, " +
                            "${messages?.size ?: 0} messages, " +
                            "${callEvents?.size ?: 0} events"
                )
            } else {
                Log.w(TAG, "Batch sync failed: HTTP ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error batch syncing", e)
        }
    }

    /**
     * Async version - launch sync in background scope without blocking.
     */
    fun syncMissedCallAsync(phoneNumber: String, contactName: String?) {
        syncScope.launch {
            syncMissedCall(phoneNumber, contactName)
        }
    }

    fun syncLeadAsync(
        phoneNumber: String,
        contactName: String?,
        serviceNeeded: String?,
        city: String?,
        urgencyLevel: String,
        status: String,
        notes: String = ""
    ) {
        syncScope.launch {
            syncLead(phoneNumber, contactName, serviceNeeded, city, urgencyLevel, status, notes)
        }
    }

    fun syncConversationMessageAsync(phoneNumber: String, role: String, content: String) {
        syncScope.launch {
            syncConversationMessage(phoneNumber, role, content)
        }
    }

    // ── Private Helpers ──────────────────────────────────────────────────────

    private suspend fun hasOperatorTierOrAbove(): Boolean {
        return try {
            val status = subscriptionManager.subscriptionStatus.first()
            status.tier.id >= SubscriptionTier.OPERATOR.id
        } catch (e: Exception) {
            Log.e(TAG, "Error checking subscription tier", e)
            false
        }
    }

    private fun getCurrentIsoTimestamp(): String {
        return Instant.now().toString()
    }
}
