package com.leadshield.app.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.leadshield.app.data.*
import com.leadshield.app.worker.SmsWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles SMS sending logic with validation, debounce, and limit enforcement.
 * Schedules SmsWorker for the actual delivery to ensure reliability.
 */
@Singleton
class SmsHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: AppPreferences,
    private val historyManager: CallHistoryManager,
    private val contactRepository: ContactRepository,
    private val subscriptionManager: SubscriptionManager,
    private val analyticsDao: AnalyticsDao
) {

    companion object {
        private const val TAG = "SmsHandler"
    }

    /**
     * Process a missed call and send auto-reply if appropriate.
     * This is the main entry point called by the CallReceiver.
     *
     * @param phoneNumber The phone number that missed called
     * @return true if SMS was sent, false otherwise
     */
    suspend fun processMissedCall(phoneNumber: String): Boolean {
        try {
            Log.i(TAG, "Processing missed call from: $phoneNumber")

            // GOD MODE OVERRIDE: If this is the master version, skip all checks except basic validity
            val isGodMode = prefs.isMasterGodModeSync()
            
            // Validate phone number basics
            if (!PhoneNumberValidator.isValid(phoneNumber)) {
                Log.w(TAG, "Invalid or blocked phone number: $phoneNumber")
                return false
            }

            // Check if app is enabled
            if (!prefs.isEnabledSync()) {
                Log.d(TAG, "Auto-reply is disabled")
                logSkipped(phoneNumber, "DISABLED")
                return false
            }

            // Check if within active hours (Always on for God Mode)
            if (!isGodMode && !prefs.isWithinActiveHours()) {
                Log.d(TAG, "Outside active hours")
                logSkipped(phoneNumber, "OUTSIDE_HOURS")
                return false
            }

            // Commercial Logic
            if (!isGodMode) {
                // 1. Check Trial Status
                if (prefs.isTrialExpiredSync() && !prefs.isUnlimitedSync()) {
                    Log.w(TAG, "Trial expired and no subscription found")
                    logSkipped(phoneNumber, "TRIAL_EXPIRED")
                    return false
                }

                // 2. Check and Reset Usage
                prefs.checkAndResetWeeklyUsage()

                // 3. Check Subscription Limits
                if (prefs.hasReachedLimit()) {
                    Log.w(TAG, "Tier limit reached")
                    logSkipped(phoneNumber, "LIMIT_REACHED")
                    return false
                }
            }

            // Contact Filter Logic (Optional Spam Shield)
            if (prefs.isReplyToContactsOnlySync()) {
                val name = getContactName(phoneNumber)
                if (name == null) {
                    Log.d(TAG, "Number $phoneNumber not in contacts, skipping (Spam Shield active)")
                    logSkipped(phoneNumber, "NOT_IN_CONTACTS")
                    return false
                }
            }

            // Check debounce (anti-flood)
            if (!prefs.canSendToNumber(phoneNumber)) {
                Log.d(TAG, "Recently texted this number, skipping (debounce)")
                logSkipped(phoneNumber, "DEBOUNCE")
                return false
            }

            // 4. Humanization Delay (Elite Feature)
            // If delay is enabled, we schedule a worker with an initial delay.
            // If no delay, we still schedule a worker to ensure reliability
            // or we could send it directly. For consistency, we use WorkManager.
            
            val delaySeconds = if (prefs.isHumanDelayEnabledSync()) {
                prefs.getHumanDelaySecondsSync()
            } else 0

            // 5. Tier-Aware Message Selection
            var message = contactRepository.getCustomMessageForNumber(phoneNumber)
            if (message == null) {
                // Check if we're outside active hours and an after-hours message is set
                val afterHoursMsg = prefs.getAfterHoursMessageSync()
                val withinHours = isGodMode || prefs.isWithinActiveHours()
                message = if (!withinHours && afterHoursMsg.isNotBlank()) {
                    afterHoursMsg
                } else {
                    prefs.getMessageSync()
                }
            }

            if (message.isNullOrBlank()) {
                Log.w(TAG, "Message is blank, cannot send")
                logSkipped(phoneNumber, "BLANK_MESSAGE")
                return false
            }

            // Schedule the Worker
            val contactName = getContactName(phoneNumber)
            val inputData = Data.Builder()
                .putString(SmsWorker.KEY_PHONE_NUMBER, phoneNumber)
                .putString(SmsWorker.KEY_MESSAGE, message)
                .putBoolean(SmsWorker.KEY_IS_GOD_MODE, isGodMode)
                .putString(SmsWorker.KEY_CONTACT_NAME, contactName)
                .putLong(SmsWorker.KEY_DELAY_MS, delaySeconds.toLong() * 1000)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<SmsWorker>()
                .setInitialDelay(delaySeconds.toLong(), TimeUnit.SECONDS)
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
            
            Log.i(TAG, "Scheduled SMS worker for $phoneNumber with ${delaySeconds}s delay")
            return true

        } catch (e: Exception) {
            Log.e(TAG, "Error processing missed call", e)
            return false
        }
    }

    private suspend fun logSkipped(phoneNumber: String, reason: String) {
        analyticsDao.insert(
            AnalyticsEntry(
                phoneNumber = phoneNumber,
                status = "SKIPPED",
                failureReason = reason,
                subscriptionTier = prefs.getSubscriptionTierSync().name
            )
        )
    }

    /**
     * Internal method used by Worker to perform the actual send.
     */
    fun rawSendSms(phoneNumber: String, message: String): Boolean {
        return sendSms(phoneNumber, message)
    }

    /**
     * Send SMS using Android SmsManager.
     * Upgraded to use sendMultipartTextMessage for God Mode reliability.
     */
    private fun sendSms(phoneNumber: String, message: String): Boolean {
        return try {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "SEND_SMS permission not granted")
                return false
            }

            val smsManager = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                context.getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }

            // God Mode: Use multipart to avoid failure on long business messages
            val parts = smsManager.divideMessage(message)
            if (parts.size > 1) {
                smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null)
            } else {
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            }

            Log.i(TAG, "SMS sent to $phoneNumber (${parts.size} parts)")
            true

        } catch (e: Exception) {
            Log.e(TAG, "Failed to send SMS to $phoneNumber", e)
            false
        }
    }

    /**
     * Get contact name from phone number.
     *
     * @param phoneNumber The phone number to lookup
     * @return Contact name if found, null otherwise
     */
    private fun getContactName(phoneNumber: String): String? {
        return try {
            val uri = ContactsContract.PhoneLookup.CONTENT_FILTER_URI.buildUpon()
                .appendPath(phoneNumber)
                .build()

            val cursor = context.contentResolver.query(
                uri,
                arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME),
                null,
                null,
                null
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        return it.getString(nameIndex)
                    }
                }
            }
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error getting contact name", e)
            null
        }
    }
}
