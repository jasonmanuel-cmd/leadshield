package com.leadshield.app.data.network

import retrofit2.Response
import retrofit2.http.*

// ── Supabase REST API ─────────────────────────────────────────────────────────

interface SupabaseRestApi {

    @POST("rest/v1/leads")
    suspend fun upsertLead(
        @Header("Authorization") token: String,
        @Header("apikey") apiKey: String,
        @Header("Prefer") prefer: String = "resolution=merge-duplicates",
        @Body lead: SupabaseLeadDto
    ): Response<Unit>

    @POST("rest/v1/conversation_messages")
    suspend fun insertMessage(
        @Header("Authorization") token: String,
        @Header("apikey") apiKey: String,
        @Body message: SupabaseMessageDto
    ): Response<Unit>

    @POST("rest/v1/call_events")
    suspend fun insertCallEvent(
        @Header("Authorization") token: String,
        @Header("apikey") apiKey: String,
        @Body event: SupabaseCallEventDto
    ): Response<Unit>
}

// ── Supabase Auth API ─────────────────────────────────────────────────────────

interface SupabaseAuthApi {

    @POST("auth/v1/signup")
    suspend fun signUp(
        @Header("apikey") apiKey: String,
        @Body request: SupabaseAuthRequest
    ): Response<SupabaseAuthResponse>

    @POST("auth/v1/token")
    suspend fun signIn(
        @Header("apikey") apiKey: String,
        @Query("grant_type") grantType: String = "password",
        @Body request: SupabaseAuthRequest
    ): Response<SupabaseAuthResponse>
}

// ── DTOs ──────────────────────────────────────────────────────────────────────

data class SupabaseLeadDto(
    val phone_number: String,
    val contact_name: String?,
    val service_needed: String?,
    val city: String?,
    val urgency_level: String,
    val status: String,
    val notes: String,
    val created_at: Long,
    val updated_at: Long,
    val called_back_at: Long?,
    val user_id: String
)

data class SupabaseMessageDto(
    val phone_number: String,
    val lead_id: String?,
    val role: String,
    val content: String,
    val timestamp: Long,
    val user_id: String
)

data class SupabaseCallEventDto(
    val phone_number: String,
    val contact_name: String?,
    val ai_handled: Boolean,
    val occurred_at: Long,
    val user_id: String
)

data class SupabaseAuthRequest(
    val email: String,
    val password: String
)

data class SupabaseAuthResponse(
    val access_token: String?,
    val user: SupabaseUser?
)

data class SupabaseUser(
    val id: String?,
    val email: String?
)

// ── LeadShield API (Next.js Backend) ──────────────────────────────────────────

/**
 * LeadShield command center sync API.
 * 
 * Deployed at: YOUR_NEXT_JS_URL/api/sync (e.g., https://crm.leadshield.io/api/sync)
 * 
 * Authenticates with Bearer token from LEADSHIELD_SYNC_TOKEN.
 * Token is specific to each customer and generated when they upgrade to OPERATOR tier.
 */
interface LeadShieldApi {

    /**
     * POST /api/sync - Sync leads, messages, and call events to the command center.
     * 
     * Request:
     * - Authorization: Bearer {LEADSHIELD_SYNC_TOKEN}
     * - Body: LeadShieldSyncPayload with user_id + one or more data types
     * 
     * Response on success (200):
     * {
     *   "ok": true,
     *   "summary": {
     *     "leadsReceived": 1,
     *     "leadsCreated": 1,
     *     "leadsUpdated": 0,
     *     "messagesReceived": 1,
     *     "messagesInserted": 0,
     *     "messagesUpdated": 1,
     *     "callEventsReceived": 1,
     *     "callEventsInserted": 1,
     *     "callEventsUpdated": 0
     *   }
     * }
     */
    @POST("api/sync")
    suspend fun syncToLeadShield(
        @Header("Authorization") authorization: String,
        @Body payload: LeadShieldSyncPayload
    ): Response<LeadShieldSyncResponse>
}

/**
 * Sync payload for LeadShield command center.
 * 
 * Fields:
 * - user_id: Required. UUID of the authenticated Supabase user.
 * - leads: Optional. List of lead objects to upsert.
 * - conversation_messages: Optional. List of message objects to sync.
 * - call_events: Optional. List of call event objects to log.
 * 
 * The server accepts flexible payload structure:
 * - Accepts both camelCase and snake_case field names
 * - Accepts single object or array for each data type
 * - Upserts by (user_id, phone_number) for leads
 * - Upserts by (user_id, phone_number, sent_at) for messages
 * - Inserts call events with no deduplication
 */
data class LeadShieldSyncPayload(
    val user_id: String,
    val leads: List<Map<String, Any?>>? = null,
    val conversation_messages: List<Map<String, Any?>>? = null,
    val call_events: List<Map<String, Any?>>? = null
)

/**
 * Response from LeadShield sync endpoint.
 * 
 * ok: true if all operations succeeded
 * summary: Stats on what was received and upserted
 */
data class LeadShieldSyncResponse(
    val ok: Boolean,
    val summary: LeadShieldSyncSummary?
)

data class LeadShieldSyncSummary(
    val leadsReceived: Int = 0,
    val leadsCreated: Int = 0,
    val leadsUpdated: Int = 0,
    val messagesReceived: Int = 0,
    val messagesInserted: Int = 0,
    val messagesUpdated: Int = 0,
    val callEventsReceived: Int = 0,
    val callEventsInserted: Int = 0,
    val callEventsUpdated: Int = 0
)
