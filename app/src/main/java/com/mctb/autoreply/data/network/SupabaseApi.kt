package com.mctb.autoreply.data.network

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
