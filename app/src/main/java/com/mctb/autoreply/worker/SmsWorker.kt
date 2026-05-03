package com.mctb.autoreply.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mctb.autoreply.data.*
import com.mctb.autoreply.util.SmsHandler
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import android.util.Log

@HiltWorker
class SmsWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val prefs: AppPreferences,
    private val historyManager: CallHistoryManager,
    private val auditRepository: AuditRepository,
    private val smsHandler: SmsHandler,
    private val analyticsDao: AnalyticsDao
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "SmsWorker"
        const val KEY_PHONE_NUMBER = "phone_number"
        const val KEY_MESSAGE = "message"
        const val KEY_IS_GOD_MODE = "is_god_mode"
        const val KEY_CONTACT_NAME = "contact_name"
        const val KEY_DELAY_MS = "delay_ms"
    }

    override suspend fun doWork(): Result {
        val phoneNumber = inputData.getString(KEY_PHONE_NUMBER) ?: return Result.failure()
        val message = inputData.getString(KEY_MESSAGE) ?: return Result.failure()
        val isGodMode = inputData.getBoolean(KEY_IS_GOD_MODE, false)
        val contactName = inputData.getString(KEY_CONTACT_NAME)
        val delayMs = inputData.getLong(KEY_DELAY_MS, 0)
        val tier = prefs.getSubscriptionTierSync().name

        Log.i(TAG, "Worker started for $phoneNumber")

        return try {
            val success = smsHandler.rawSendSms(phoneNumber, message)

            if (success) {
                if (!isGodMode) {
                    prefs.incrementAutoTextCount()
                }
                
                prefs.recordTextSent(phoneNumber)
                historyManager.addEntry(
                    phoneNumber = phoneNumber,
                    contactName = contactName,
                    messageSent = message,
                    wasRead = false
                )

                analyticsDao.insert(
                    AnalyticsEntry(
                        phoneNumber = phoneNumber,
                        status = "SUCCESS",
                        responseDelayMs = delayMs,
                        subscriptionTier = tier
                    )
                )
                
                Log.i(TAG, "Worker: SMS Sent successfully")
                Result.success()
            } else {
                analyticsDao.insert(
                    AnalyticsEntry(
                        phoneNumber = phoneNumber,
                        status = "FAILURE",
                        failureReason = "SmsManager error",
                        responseDelayMs = delayMs,
                        subscriptionTier = tier
                    )
                )
                Log.e(TAG, "Worker: SMS failed to send")
                // Use failure (not retry) — SmsManager errors are usually permanent
                // (e.g. permission revoked, SIM absent). Retrying would spam the queue.
                Result.failure()
            }
        } catch (e: Exception) {
            analyticsDao.insert(
                AnalyticsEntry(
                    phoneNumber = phoneNumber,
                    status = "FAILURE",
                    failureReason = e.message ?: "Unknown error",
                    responseDelayMs = delayMs,
                    subscriptionTier = tier
                )
            )
            Log.e(TAG, "Worker: Critical error", e)
            Result.failure()
        }
    }
}
