package com.leadshield.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ConversationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertConversation(conversation: ConversationEntity)

    @Query("SELECT * FROM ai_conversations WHERE phoneNumber = :phoneNumber LIMIT 1")
    suspend fun getConversation(phoneNumber: String): ConversationEntity?

    @Query("SELECT * FROM ai_conversation_messages WHERE phoneNumber = :phoneNumber ORDER BY timestamp ASC")
    suspend fun getMessagesForNumber(phoneNumber: String): List<ConversationMessageEntity>

    @Insert
    suspend fun insertMessage(message: ConversationMessageEntity)

    @Query("UPDATE ai_conversations SET isActive = 0 WHERE phoneNumber = :phoneNumber")
    suspend fun closeConversation(phoneNumber: String)

    @Query("SELECT * FROM ai_conversations WHERE isActive = 1 AND startedAt >= :since")
    suspend fun getActiveConversationsSince(since: Long): List<ConversationEntity>
}
