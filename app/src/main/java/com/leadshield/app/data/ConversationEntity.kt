package com.leadshield.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_conversations")
data class ConversationEntity(
    @PrimaryKey val phoneNumber: String,
    val startedAt: Long,
    val lastMessageAt: Long,
    val isActive: Boolean,
    val exchangeCount: Int  // number of AI reply rounds (cap at 4)
)
