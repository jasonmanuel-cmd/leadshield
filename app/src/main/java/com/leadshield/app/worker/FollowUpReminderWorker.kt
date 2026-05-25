package com.leadshield.app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.leadshield.app.R
import com.leadshield.app.data.AppDatabase
import com.leadshield.app.data.AppPreferences
import com.leadshield.app.data.SubscriptionTier
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class FollowUpReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val prefs: AppPreferences,
    private val db: AppDatabase
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val CHANNEL_ID = "followup_reminders"
        private const val WORK_NAME = "followup_reminder_periodic"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<FollowUpReminderWorker>(1, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()
                )
                .addTag("followup_reminder")
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }

    override suspend fun doWork(): Result {
        val tier = prefs.getSubscriptionTierSync()
        if (tier != SubscriptionTier.OPERATOR && tier != SubscriptionTier.MASTER) {
            return Result.success()
        }

        // Default callback window: 60 minutes
        val callbackWindowMs = 60L * 60 * 1000
        val overdueThreshold = System.currentTimeMillis() - callbackWindowMs

        val overdueLeads = db.leadCardDao().getOverdueLeads(olderThan = overdueThreshold)

        if (overdueLeads.isEmpty()) return Result.success()

        createNotificationChannel()

        overdueLeads.forEachIndexed { index, lead ->
            val name = lead.contactName ?: lead.phoneNumber
            val need = lead.serviceNeeded ?: "something"
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Callback Overdue")
                .setContentText("You haven't called $name back yet — they said $need")
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText("You haven't called $name back yet — they said $need"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(1000 + index, notification)
        }

        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Follow-up Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminds you when leads haven't been called back"
            }
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }
}
