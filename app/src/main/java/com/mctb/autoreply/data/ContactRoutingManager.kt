package com.mctb.autoreply.data

import android.content.Context
import android.provider.ContactsContract
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Routes incoming phone numbers to their appropriate contact category.
 * Used by the Operator tier to send different messages to VIPs, known contacts, and new leads.
 */
sealed class CallRoute {
    data class VipContact(val name: String, val nickname: String) : CallRoute()
    data class KnownContact(val name: String) : CallRoute()
    object UnknownLead : CallRoute()
}

@Singleton
class ContactRoutingManager @Inject constructor(
    private val db: AppDatabase
) {

    /**
     * Determines the route for a given phone number.
     * Priority: VIP table → Phone contacts → Unknown lead
     */
    suspend fun getRouteForNumber(phoneNumber: String, context: Context): CallRoute {
        val normalized = normalizePhone(phoneNumber)

        // 1. Check VIP table first
        val vip = db.vipContactDao().getByPhone(normalized)
            ?: db.vipContactDao().getByPhone(phoneNumber)
        if (vip != null) {
            return CallRoute.VipContact(name = vip.contactName, nickname = vip.nickname)
        }

        // 2. Check phone contacts
        val contactName = lookupPhoneContact(context, phoneNumber)
        if (contactName != null) {
            return CallRoute.KnownContact(name = contactName)
        }

        // 3. Unknown lead
        return CallRoute.UnknownLead
    }

    /**
     * Resolves a display name from the phone's contacts database via ContentResolver.
     */
    private fun lookupPhoneContact(context: Context, phoneNumber: String): String? {
        return try {
            val uri = android.net.Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                android.net.Uri.encode(phoneNumber)
            )
            val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME))
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }

    /** Strip non-digit characters for consistent lookup. */
    private fun normalizePhone(number: String): String = number.filter { it.isDigit() }
}
