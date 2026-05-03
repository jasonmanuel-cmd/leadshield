package com.mctb.autoreply.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalyticsDao {
    @Insert
    suspend fun insert(entry: AnalyticsEntry)

    @Query("SELECT * FROM analytics_events ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentEvents(limit: Int = 100): Flow<List<AnalyticsEntry>>

    @Query("SELECT COUNT(*) FROM analytics_events WHERE status = 'SUCCESS'")
    fun getSuccessCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM analytics_events")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT * FROM analytics_events WHERE timestamp >= :sinceTimestamp")
    suspend fun getEventsSince(sinceTimestamp: Long): List<AnalyticsEntry>

    @Query("DELETE FROM analytics_events WHERE timestamp < :timestamp")
    suspend fun clearOldEvents(timestamp: Long)
}
