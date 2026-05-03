package com.mctb.autoreply.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.mctb.autoreply.data.AppDatabase
import com.mctb.autoreply.data.AppPreferences
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class ReviewRequestWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val prefs: AppPreferences,
    private val db: AppDatabase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val phoneNumber = inputData.getString("phone_number") ?: return Result.failure()
        val contactName = inputData.getString("contact_name") ?: "there"
        val reviewUrl = prefs.googleReviewUrl.first()

        if (reviewUrl.isBlank()) return Result.success()

        val tier = prefs.getSubscriptionTierSync()
        if (!tier.hasAiConversation) return Result.success() // Operator+ only

        val businessName = prefs.businessName.first().ifBlank { "us" }
        val message = "Hey $contactName, thanks for choosing $businessName! " +
            "If we took good care of you, a quick Google review means the world to a small business — " +
            "takes 30 seconds: $reviewUrl"

        return try {
            val smsManager = if (android.os.Build.VERSION.SDK_INT >= 31) {
                applicationContext.getSystemService(android.telephony.SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                android.telephony.SmsManager.getDefault()
            }
            smsManager?.sendTextMessage(phoneNumber, null, message, null, null)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        fun schedule(context: Context, phoneNumber: String, contactName: String?) {
            val data = workDataOf(
                "phone_number" to phoneNumber,
                "contact_name" to (contactName ?: "there")
            )
            val request = OneTimeWorkRequestBuilder<ReviewRequestWorker>()
                .setInitialDelay(24, java.util.concurrent.TimeUnit.HOURS)
                .setInputData(data)
                .addTag("review_request_$phoneNumber")
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                "review_$phoneNumber",
                ExistingWorkPolicy.KEEP,
                request
            )
        }
    }
}
