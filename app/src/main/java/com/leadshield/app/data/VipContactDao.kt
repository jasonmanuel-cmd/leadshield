package com.leadshield.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VipContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(contact: VipContactEntity)

    @Delete
    suspend fun delete(contact: VipContactEntity)

    @Query("DELETE FROM vip_contacts WHERE phoneNumber = :phoneNumber")
    suspend fun deleteByPhone(phoneNumber: String)

    @Query("SELECT * FROM vip_contacts ORDER BY contactName ASC")
    fun getAll(): Flow<List<VipContactEntity>>

    @Query("SELECT * FROM vip_contacts WHERE phoneNumber = :phoneNumber LIMIT 1")
    suspend fun getByPhone(phoneNumber: String): VipContactEntity?

    @Update
    suspend fun update(contact: VipContactEntity)
}
