package com.leadshield.app.data

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.leadshield.app.BuildConfig
import com.leadshield.app.data.network.NeonDataApi
import com.leadshield.app.data.network.SqlRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the synchronization of audit logs to the cloud (Neon Postgres).
 * This is a Master Tier-exclusive feature for remote monitoring.
 */
@Singleton
class CloudSyncManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TAG = "CloudSyncManager"

    /**
     * Schedules a background sync task.
     */
    fun scheduleSync() {
        if (BuildConfig.NEON_API_KEY.isBlank() || BuildConfig.NEON_DATA_API_URL.isBlank()) {
            Log.w(TAG, "Cloud sync skipped: Neon API config missing")
            return
        }

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<CloudSyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
            .addTag("cloud_sync")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "master_audit_sync",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
        Log.i(TAG, "Cloud Sync scheduled for Master Tier")
    }
}

/**
 * The background worker that performs the actual network push.
 */
@HiltWorker
class CloudSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val auditDao: AuditDao,
    private val neonDataApi: NeonDataApi
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            if (BuildConfig.NEON_API_KEY.isBlank()) {
                Log.w("CloudSyncWorker", "NEON_API_KEY missing; cannot sync.")
                return Result.failure()
            }

            val unsynced = auditDao.getUnsyncedEntries(limit = 50)
            if (unsynced.isEmpty()) {
                Log.i("CloudSyncWorker", "No unsynced audit entries found.")
                return Result.success()
            }

            val syncedIds = mutableListOf<Int>()
            val authHeader = "Bearer ${BuildConfig.NEON_API_KEY}"

            for (entry in unsynced) {
                val request = SqlRequest(
                    sql = """
                        INSERT INTO audit_log (timestamp, is_pfc_stable, mood_intensity, is_synced)
                        VALUES (?, ?, ?, ?)
                    """.trimIndent(),
                    params = listOf(
                        entry.timestamp,
                        entry.isPfcStable,
                        entry.moodIntensity,
                        true
                    )
                )

                val response = neonDataApi.executeSql(authHeader, request)
                if (!response.isSuccessful) {
                    Log.e("CloudSyncWorker", "Failed to sync entry ${entry.id}: HTTP ${response.code()}")
                    return Result.retry()
                }
                syncedIds.add(entry.id)
            }

            if (syncedIds.isNotEmpty()) {
                auditDao.markEntriesSynced(syncedIds)
            }

            Log.i("CloudSyncWorker", "Synced ${syncedIds.size} audit entries.")
            Result.success()
        } catch (e: Exception) {
            Log.e("CloudSyncWorker", "Sync failed", e)
            Result.retry()
        }
    }
}
