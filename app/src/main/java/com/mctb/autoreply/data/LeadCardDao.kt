package com.mctb.autoreply.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LeadCardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(lead: LeadCardEntity)

    @Query("SELECT * FROM lead_cards ORDER BY createdAt DESC")
    fun getAll(): Flow<List<LeadCardEntity>>

    @Query("SELECT * FROM lead_cards WHERE phoneNumber = :phoneNumber LIMIT 1")
    suspend fun getByPhone(phoneNumber: String): LeadCardEntity?

    @Query("UPDATE lead_cards SET status = :status, updatedAt = :updatedAt WHERE phoneNumber = :phoneNumber")
    suspend fun updateStatus(phoneNumber: String, status: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE lead_cards SET notes = :notes, updatedAt = :updatedAt WHERE phoneNumber = :phoneNumber")
    suspend fun updateNotes(phoneNumber: String, notes: String, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT * FROM lead_cards WHERE status = :status ORDER BY createdAt DESC")
    fun getByStatus(status: String): Flow<List<LeadCardEntity>>

    @Query("UPDATE lead_cards SET calledBackAt = :calledBackAt, status = 'called_back', updatedAt = :updatedAt WHERE phoneNumber = :phoneNumber")
    suspend fun markCalledBack(phoneNumber: String, calledBackAt: Long = System.currentTimeMillis(), updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT * FROM lead_cards WHERE status = 'new' AND createdAt < :olderThan AND calledBackAt IS NULL")
    suspend fun getOverdueLeads(olderThan: Long): List<LeadCardEntity>

    @Query("SELECT * FROM lead_cards WHERE createdAt >= :since ORDER BY createdAt DESC")
    suspend fun getLeadsSince(since: Long): List<LeadCardEntity>

    @Query("UPDATE lead_cards SET leadScore = :score WHERE phoneNumber = :phoneNumber")
    suspend fun updateLeadScore(phoneNumber: String, score: Int)

    @Query("SELECT * FROM lead_cards ORDER BY leadScore DESC, updatedAt DESC")
    fun getAllLeadsScoredDesc(): Flow<List<LeadCardEntity>>
}
