package com.mctb.autoreply.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_log")
data class AuditEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isPfcStable: Boolean, // True if Cipher tagged [PFC STABLE]
    val moodIntensity: Int, // 1-10 based on the text analysis
    val isSynced: Boolean = false
)
