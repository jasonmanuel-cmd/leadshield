package com.mctb.autoreply.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Expert Tier Feature: Custom messages per contact.
 */
@Entity(tableName = "contact_messages")
data class ContactMessage(
    @PrimaryKey val phoneNumber: String, // Phone number as the key
    val customMessage: String,
    val contactName: String? = null,
    val lastModifiedAt: Long = System.currentTimeMillis()
)
