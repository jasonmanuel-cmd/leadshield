package com.mctb.autoreply.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vip_contacts")
data class VipContactEntity(
    @PrimaryKey val phoneNumber: String,
    val contactName: String,
    val nickname: String,        // e.g. "Wife", "Mom", "Big Bob"
    val messageType: String,     // "FAMILY" | "CLIENT" | "CUSTOM"
    val customMessage: String = "" // only used if messageType == CUSTOM
)
