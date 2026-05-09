package com.leadshield.app.data

data class WeeklyReport(
    val stabilityScore: Float,
    val flowConsistency: Float,
    val status: String,
    val statusColorHex: String,
    val recommendation: String,
    val amygdalaHijackCount: Int
)

class AuditRepository(
    private val auditDao: AuditDao,
    private val prefs: AppPreferences,
    private val cloudSyncManager: CloudSyncManager
) {

    suspend fun insert(entry: AuditEntry) {
        auditDao.insert(entry)
        
        // Trigger Cloud Sync for Master Tier users
        if (prefs.isMasterGodModeSync() || prefs.getSubscriptionTierSync() == SubscriptionTier.MASTER) {
            cloudSyncManager.scheduleSync()
        }
    }

    suspend fun generateWeeklyReport(): WeeklyReport {
        val lastSevenDays = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
        val entries = auditDao.getEntriesSince(lastSevenDays)

        val pfcCount = entries.count { it.isPfcStable }
        val highIntensityCount = entries.count { it.moodIntensity > 7 }
        
        val stabilityPercentage = if (entries.isNotEmpty()) {
            (pfcCount.toFloat() / entries.size.toFloat()) * 100
        } else 0f

        val avgMood = if (entries.isNotEmpty()) entries.map { it.moodIntensity }.average().toFloat() else 5f

        return WeeklyReport(
            stabilityScore = stabilityPercentage,
            flowConsistency = (avgMood / 10f) * 100,
            status = when {
                stabilityPercentage > 85 -> "TITAN-LEVEL SOVEREIGNTY"
                stabilityPercentage > 60 -> "CORE SYSTEM: BALANCED"
                stabilityPercentage > 30 -> "VOLATILE: AMYGDALA ACTIVE"
                else -> "CRITICAL: SYSTEM COMPROMISED"
            },
            statusColorHex = when {
                stabilityPercentage > 85 -> "#00E676" // Green
                stabilityPercentage > 60 -> "#2979FF" // Blue
                stabilityPercentage > 30 -> "#FFAB40" // Orange
                else -> "#FF5252" // Red
            },
            recommendation = when {
                stabilityPercentage > 85 -> "Continue the build. You are optimizing."
                stabilityPercentage > 60 -> "Stable output. Increase logic-based inputs."
                stabilityPercentage > 30 -> "Identify the trigger. Eliminate the social noise."
                else -> "INITIATE COLD RESET. High volatility detected."
            },
            amygdalaHijackCount = highIntensityCount
        )
    }
}
