package com.mctb.autoreply.data

import android.util.Log
import com.mctb.autoreply.data.network.SupabaseCallEventDto
import com.mctb.autoreply.data.network.SupabaseLeadDto
import com.mctb.autoreply.data.network.SupabaseMessageDto
import com.mctb.autoreply.data.network.SupabaseRestApi
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "SupabaseSyncManager"
private const val ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImV4cGNpbndkeHhsZmdrdXhwaXJxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzc2ODc2MDUsImV4cCI6MjA5MzI2MzYwNX0.O3N5yhqVwhsmgzuBiJyTS2s7BeAtV7gDYhee0my7C6M"

@Singleton
class SupabaseSyncManager @Inject constructor(
    private val api: SupabaseRestApi,
    private val prefs: AppPreferences
) {

    suspend fun syncLead(lead: LeadCardEntity, userId: String) {
        val token = prefs.getSupabaseTokenSync()
        if (token.isBlank() || userId.isBlank()) return
        try {
            val response = api.upsertLead(
                token = "Bearer $token",
                apiKey = ANON_KEY,
                lead = SupabaseLeadDto(
                    phone_number = lead.phoneNumber,
                    contact_name = lead.contactName,
                    service_needed = lead.serviceNeeded,
                    city = lead.city,
                    urgency_level = lead.urgencyLevel,
                    status = lead.status,
                    notes = lead.notes,
                    created_at = lead.createdAt,
                    updated_at = lead.updatedAt,
                    called_back_at = lead.calledBackAt,
                    user_id = userId
                )
            )
            if (!response.isSuccessful) {
                Log.w(TAG, "syncLead failed: HTTP ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "syncLead error", e)
        }
    }

    suspend fun syncMessage(
        msg: ConversationMessageEntity,
        leadId: String?,
        userId: String
    ) {
        val token = prefs.getSupabaseTokenSync()
        if (token.isBlank() || userId.isBlank()) return
        try {
            val response = api.insertMessage(
                token = "Bearer $token",
                apiKey = ANON_KEY,
                message = SupabaseMessageDto(
                    phone_number = msg.phoneNumber,
                    lead_id = leadId,
                    role = msg.role,
                    content = msg.content,
                    timestamp = msg.timestamp,
                    user_id = userId
                )
            )
            if (!response.isSuccessful) {
                Log.w(TAG, "syncMessage failed: HTTP ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "syncMessage error", e)
        }
    }

    suspend fun syncCallEvent(
        phone: String,
        contactName: String?,
        aiHandled: Boolean,
        userId: String
    ) {
        val token = prefs.getSupabaseTokenSync()
        if (token.isBlank() || userId.isBlank()) return
        try {
            val response = api.insertCallEvent(
                token = "Bearer $token",
                apiKey = ANON_KEY,
                event = SupabaseCallEventDto(
                    phone_number = phone,
                    contact_name = contactName,
                    ai_handled = aiHandled,
                    occurred_at = System.currentTimeMillis(),
                    user_id = userId
                )
            )
            if (!response.isSuccessful) {
                Log.w(TAG, "syncCallEvent failed: HTTP ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "syncCallEvent error", e)
        }
    }
}
