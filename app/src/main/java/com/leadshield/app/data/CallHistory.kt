package com.leadshield.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Simple data class for call history entries.
 */
data class CallHistoryEntry(
    val phoneNumber: String,
    val contactName: String?,
    val timestamp: Long,
    val messageSent: String,
    val wasRead: Boolean = false,
    val status: String = "NEW", // NEW, HOT, DONE, SPAM
    val manualName: String? = null // For when the user manually adds a name
) {
    fun getFormattedTime(): String {
        val sdf = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun getDisplayName(): String {
        return manualName ?: contactName ?: phoneNumber
    }

    // Convert to/from string for storage
    fun toStorageString(): String {
        return "$phoneNumber|${contactName ?: ""}|$timestamp|$messageSent|$wasRead|$status|${manualName ?: ""}"
    }

    companion object {
        fun fromStorageString(str: String): CallHistoryEntry? {
            return try {
                val parts = str.split("|")
                if (parts.size >= 5) {
                    CallHistoryEntry(
                        phoneNumber = parts[0],
                        contactName = parts[1].ifEmpty { null },
                        timestamp = parts[2].toLong(),
                        messageSent = parts[3],
                        wasRead = parts[4].toBoolean(),
                        status = if (parts.size >= 6) parts[5] else "NEW",
                        manualName = if (parts.size >= 7) parts[6].ifEmpty { null } else null
                    )
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }
}

/**
 * Manager for call history storage.
 * Keeps track of the last 20 auto-replies sent.
 */
class CallHistoryManager(private val context: Context) {

    companion object {
        private val Context.historyDataStore: DataStore<Preferences> by preferencesDataStore(name = "call_history")
        private val KEY_HISTORY = stringPreferencesKey("history_data")
        private const val MAX_HISTORY_SIZE = 20
        private const val ENTRY_SEPARATOR = "|||"
    }

    /**
     * Flow of call history entries.
     */
    val history: Flow<List<CallHistoryEntry>> = context.historyDataStore.data.map { prefs ->
        val dataString = prefs[KEY_HISTORY] ?: ""
        try {
            if (dataString.isEmpty()) {
                emptyList()
            } else {
                dataString.split(ENTRY_SEPARATOR)
                    .mapNotNull { CallHistoryEntry.fromStorageString(it) }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Add a new call history entry.
     */
    suspend fun addEntry(
        phoneNumber: String,
        contactName: String?,
        messageSent: String,
        wasRead: Boolean = false
    ) {
        context.historyDataStore.edit { prefs ->
            val dataString = prefs[KEY_HISTORY] ?: ""
            val existingEntries = if (dataString.isEmpty()) {
                emptyList<CallHistoryEntry>()
            } else {
                try {
                    dataString.split(ENTRY_SEPARATOR)
                        .mapNotNull { CallHistoryEntry.fromStorageString(it) }
                } catch (e: Exception) {
                    emptyList<CallHistoryEntry>()
                }
            }

            // Add new entry at the beginning
            val newEntry = CallHistoryEntry(
                phoneNumber = phoneNumber,
                contactName = contactName,
                timestamp = System.currentTimeMillis(),
                messageSent = messageSent,
                wasRead = wasRead
            )

            // Combine new entry with existing entries
            val allEntries = listOf(newEntry) + existingEntries

            // Keep only the last MAX_HISTORY_SIZE entries
            val trimmedHistory = allEntries.take(MAX_HISTORY_SIZE)

            // Save back to preferences
            val newDataString = trimmedHistory.joinToString(ENTRY_SEPARATOR) { it.toStorageString() }
            prefs[KEY_HISTORY] = newDataString
        }
    }

    /**
     * Update an entry's status or name.
     */
    suspend fun updateEntry(phoneNumber: String, timestamp: Long, newStatus: String? = null, newName: String? = null) {
        context.historyDataStore.edit { prefs ->
            val dataString = prefs[KEY_HISTORY] ?: ""
            val currentEntries = dataString.split(ENTRY_SEPARATOR)
                .mapNotNull { CallHistoryEntry.fromStorageString(it) }
            
            val updatedEntries = currentEntries.map { entry ->
                if (entry.phoneNumber == phoneNumber && entry.timestamp == timestamp) {
                    entry.copy(
                        status = newStatus ?: entry.status,
                        manualName = newName ?: entry.manualName
                    )
                } else entry
            }
            
            val newDataString = updatedEntries.joinToString(ENTRY_SEPARATOR) { it.toStorageString() }
            prefs[KEY_HISTORY] = newDataString
        }
    }

    /**
     * Clear all history.
     */
    suspend fun clearHistory() {
        context.historyDataStore.edit { prefs ->
            prefs[KEY_HISTORY] = ""
        }
    }

    /**
     * Get history synchronously (for background operations).
     */
    suspend fun getHistorySync(): List<CallHistoryEntry> {
        return history.first()
    }
}
