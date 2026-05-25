package com.leadshield.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analytics_events")
data class AnalyticsEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val phoneNumber: String,
    val status: String, // "SUCCESS", "FAILURE", "SKIPPED"
    val failureReason: String? = null,
    val responseDelayMs: Long = 0,
    val subscriptionTier: String
)
