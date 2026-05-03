package com.mctb.autoreply.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.mctb.autoreply.R
import com.mctb.autoreply.data.AppDatabase
import com.mctb.autoreply.data.AppPreferences
import com.mctb.autoreply.data.SubscriptionTier
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar
import java.util.concurrent.TimeUnit

@HiltWorker
class DailySummaryWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val prefs: AppPreferences,
    private val db: AppDatabase
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val CHANNEL_ID = "daily_summary"
        private const val WORK_NAME = "daily_summary_periodic"
        private const val NOTIFICATION_ID = 2000

        fun schedule(context: Context) {
            // Calculate initial delay to run at 6 PM
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 18)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (before(now)) add(Calendar.DAY_OF_YEAR, 1)
            }
            val initialDelayMs = target.timeInMillis - now.timeInMillis

            val request = PeriodicWorkRequestBuilder<DailySummaryWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(initialDelayMs, TimeUnit.MILLISECONDS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()
                )
                .addTag("daily_summary")
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

        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val leadDao = db.leadCardDao()
        val conversationDao = db.conversationDao()

        val todaysLeads = leadDao.getLeadsSince(startOfDay)
        val totalLeads = todaysLeads.size
        val todaysConversations = conversationDao.getActiveConversationsSince(startOfDay)
        val totalConversations = todaysConversations.size

        // Count VIP vs known vs new
        val vipReplied = todaysLeads.count { it.urgencyLevel == "normal" && it.contactName != null }
        val newLeads = todaysLeads.count { it.status == "new" }
        val awaitingCallback = todaysLeads.count { it.status == "new" && it.calledBackAt == null }

        createNotificationChannel()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val summaryText = "Today: $totalLeads missed calls → $totalLeads leads captured, " +
            "$totalConversations AI conversations. $awaitingCallback still waiting for callback."

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("LeadShield Daily Summary")
            .setContentText(summaryText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(summaryText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)

        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Daily Summary",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "End-of-day summary of your leads and AI conversations"
            }
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }
}
