package com.leadshield.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lead_cards")
data class LeadCardEntity(
    @PrimaryKey val phoneNumber: String,
    val contactName: String?,
    val serviceNeeded: String?,
    val city: String?,
    val urgencyLevel: String = "normal",
    val status: String = "new",   // new|called_back|quoted|booked|lost
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val calledBackAt: Long? = null,
    val leadScore: Int = 0  // 0=cold, 1=warm, 2=hot
)
