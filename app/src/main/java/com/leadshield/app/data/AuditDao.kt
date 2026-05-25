package com.leadshield.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AuditDao {
    @Insert
    suspend fun insert(entry: AuditEntry)

    @Query("SELECT * FROM audit_log WHERE timestamp >= :sinceTimestamp ORDER BY timestamp DESC")
    suspend fun getEntriesSince(sinceTimestamp: Long): List<AuditEntry>

    @Query("SELECT * FROM audit_log ORDER BY timestamp DESC")
    suspend fun getAllEntries(): List<AuditEntry>

    @Query("SELECT * FROM audit_log WHERE isSynced = 0 ORDER BY timestamp ASC LIMIT :limit")
    suspend fun getUnsyncedEntries(limit: Int = 50): List<AuditEntry>

    @Query("UPDATE audit_log SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markEntriesSynced(ids: List<Int>)
}
