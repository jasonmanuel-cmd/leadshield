package com.mctb.autoreply.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contact_messages")
    fun getAllContactMessages(): Flow<List<ContactMessage>>

    @Query("SELECT * FROM contact_messages WHERE phoneNumber = :phoneNumber LIMIT 1")
    suspend fun getMessageForNumber(phoneNumber: String): ContactMessage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(contactMessage: ContactMessage)

    @Delete
    suspend fun delete(contactMessage: ContactMessage)
}
