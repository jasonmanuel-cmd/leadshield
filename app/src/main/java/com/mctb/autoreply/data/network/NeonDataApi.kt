package com.mctb.autoreply.data.network

import retrofit2.Response
import retrofit2.http.*

/**
 * Interface for the Neon Data API.
 * Allows direct SQL execution via HTTP for the Master Tier Cloud Sync.
 */
interface NeonDataApi {
    
    @POST("sql")
    suspend fun executeSql(
        @Header("Authorization") token: String,
        @Body request: SqlRequest
    ): Response<SqlResponse>
}

data class SqlRequest(
    val sql: String,
    val params: List<Any>? = null
)

data class SqlResponse(
    val rows: List<Map<String, Any>>? = null,
    val command: String? = null,
    val rowCount: Int? = null
)
