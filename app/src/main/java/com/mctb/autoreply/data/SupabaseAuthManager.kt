package com.mctb.autoreply.data

import android.util.Log
import com.mctb.autoreply.data.network.SupabaseAuthApi
import com.mctb.autoreply.data.network.SupabaseAuthRequest
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "SupabaseAuthManager"
private const val ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImV4cGNpbndkeHhsZmdrdXhwaXJxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzc2ODc2MDUsImV4cCI6MjA5MzI2MzYwNX0.O3N5yhqVwhsmgzuBiJyTS2s7BeAtV7gDYhee0my7C6M"

sealed class AuthResult {
    data class Success(val token: String, val userId: String, val email: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

@Singleton
class SupabaseAuthManager @Inject constructor(
    private val authApi: SupabaseAuthApi,
    private val prefs: AppPreferences
) {

    suspend fun signUp(email: String, password: String): AuthResult {
        return try {
            val response = authApi.signUp(
                apiKey = ANON_KEY,
                request = SupabaseAuthRequest(email = email, password = password)
            )
            if (response.isSuccessful) {
                val body = response.body()
                val token = body?.access_token ?: return AuthResult.Error("No token returned")
                val userId = body.user?.id ?: return AuthResult.Error("No user ID returned")
                prefs.saveSupabaseSession(token, userId, email)
                AuthResult.Success(token, userId, email)
            } else {
                AuthResult.Error("Sign up failed: HTTP ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "signUp error", e)
            AuthResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            val response = authApi.signIn(
                apiKey = ANON_KEY,
                request = SupabaseAuthRequest(email = email, password = password)
            )
            if (response.isSuccessful) {
                val body = response.body()
                val token = body?.access_token ?: return AuthResult.Error("No token returned")
                val userId = body.user?.id ?: return AuthResult.Error("No user ID returned")
                prefs.saveSupabaseSession(token, userId, email)
                AuthResult.Success(token, userId, email)
            } else {
                AuthResult.Error("Sign in failed: HTTP ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "signIn error", e)
            AuthResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun signOut() {
        prefs.clearSupabaseSession()
    }

    suspend fun getCurrentUser(): Pair<String, String>? {
        val token = prefs.getSupabaseTokenSync()
        val userId = prefs.getSupabaseUserIdSync()
        return if (token.isNotBlank() && userId.isNotBlank()) Pair(token, userId) else null
    }
}
