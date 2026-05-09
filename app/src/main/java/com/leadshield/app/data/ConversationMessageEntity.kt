package com.leadshield.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_conversation_messages")
data class ConversationMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val phoneNumber: String,
    val role: String,       // "assistant" or "user"
    val content: String,
    val timestamp: Long
)
