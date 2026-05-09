package com.leadshield.app.data

import kotlinx.coroutines.flow.Flow

class ContactRepository(private val contactDao: ContactDao) {

    fun getAllCustomMessages(): Flow<List<ContactMessage>> {
        return contactDao.getAllContactMessages()
    }

    suspend fun getCustomMessageForNumber(phoneNumber: String): String? {
        return contactDao.getMessageForNumber(phoneNumber)?.customMessage
    }

    suspend fun updateCustomMessage(phoneNumber: String, message: String, name: String? = null) {
        val entry = ContactMessage(
            phoneNumber = phoneNumber,
            customMessage = message,
            contactName = name
        )
        contactDao.insertOrUpdate(entry)
    }

    suspend fun removeCustomMessage(phoneNumber: String) {
        val entry = contactDao.getMessageForNumber(phoneNumber)
        if (entry != null) {
            contactDao.delete(entry)
        }
    }
}
