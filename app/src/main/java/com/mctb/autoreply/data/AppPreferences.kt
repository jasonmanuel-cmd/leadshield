package com.mctb.autoreply.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Calendar

/**
 * Subscription tiers for LeadShield.
 *
 * FREE     – 10 auto-replies / month, shows ads, one-time SMS only.
 * PRO      – Unlimited auto-replies, no ads, one-time SMS only. $7.99/mo.
 * OPERATOR – Unlimited replies, no ads, AI conversation continuation + all AI features. $49/mo.
 * MASTER   – Internal/developer tier. All features, no billing required.
 * VOICE    – $99/mo, unlimited replies, AI voice answering, all Operator features.
 * TEAM     – $129/mo, 3-phone team mode, all Voice features.
 */
enum class SubscriptionTier(val id: Int) {
    FREE(0),
    PRO(1),
    OPERATOR(2),
    MASTER(3),
    VOICE(4),
    TEAM(5);

    companion object {
        fun fromId(id: Int): SubscriptionTier {
            return values().find { it.id == id } ?: FREE
        }
    }

    /** Monthly reply cap. Int.MAX_VALUE = effectively unlimited. */
    val monthlyLimit: Int
        get() = when (this) {
            FREE -> 10
            PRO -> Int.MAX_VALUE
            OPERATOR -> Int.MAX_VALUE
            MASTER -> Int.MAX_VALUE
            VOICE -> Int.MAX_VALUE
            TEAM -> Int.MAX_VALUE
        }

    val isUnlimited: Boolean
        get() = this != FREE

    val hasAiConversation: Boolean
        get() = this == OPERATOR || this == MASTER || this == VOICE || this == TEAM

    val hasVoiceAnswering: Boolean
        get() = this == VOICE || this == MASTER || this == TEAM

    val hasTeamMode: Boolean
        get() = this == TEAM || this == MASTER

    val showAds: Boolean
        get() = this == FREE
}

/**
 * DataStore-based preferences manager for app settings.
 * Provides type-safe access to all app configuration and state.
 */
class AppPreferences(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

        // Preference keys
        private val KEY_ENABLED = booleanPreferencesKey("enabled")
        private val KEY_MESSAGE = stringPreferencesKey("message")
        private val KEY_START_HOUR = intPreferencesKey("start_hour")
        private val KEY_START_MINUTE = intPreferencesKey("start_minute")
        private val KEY_END_HOUR = intPreferencesKey("end_hour")
        private val KEY_END_MINUTE = intPreferencesKey("end_minute")
        private val KEY_ALWAYS_ON = booleanPreferencesKey("always_on")
        private val KEY_AUTO_TEXT_COUNT = intPreferencesKey("auto_text_count")
        private val KEY_IS_UNLIMITED = booleanPreferencesKey("is_unlimited")
        private val KEY_PAUSED_UNTIL = longPreferencesKey("paused_until")
        private val KEY_ONBOARDED = booleanPreferencesKey("onboarded")
        private val KEY_REPLY_TO_CONTACTS_ONLY = booleanPreferencesKey("reply_to_contacts_only")
        private val KEY_TRIAL_STARTED_AT = longPreferencesKey("trial_started_at")
        private val KEY_LAST_RESET_AT = longPreferencesKey("last_reset_at")
        private val KEY_IS_MASTER_GOD_MODE = booleanPreferencesKey("is_master_god_mode")
        private val KEY_HUMAN_DELAY_ENABLED = booleanPreferencesKey("human_delay_enabled")
        private val KEY_HUMAN_DELAY_SECONDS = intPreferencesKey("human_delay_seconds")
        private val KEY_SUBSCRIPTION_TIER = intPreferencesKey("subscription_tier")

        // Message template keys (Operator tier)
        val KEY_VIP_MESSAGE = stringPreferencesKey("vip_message")
        val KEY_CONTACT_MESSAGE = stringPreferencesKey("contact_message")
        val KEY_LEAD_MESSAGE = stringPreferencesKey("lead_message")

        // Voice feature keys
        val KEY_VOICE_GREETING = stringPreferencesKey("voice_greeting")

        // Supabase auth keys
        val KEY_SUPABASE_TOKEN = stringPreferencesKey("supabase_token")
        val KEY_SUPABASE_USER_ID = stringPreferencesKey("supabase_user_id")
        val KEY_CRM_EMAIL = stringPreferencesKey("crm_email")

        // Default message templates
        const val DEFAULT_VIP_MESSAGE = "Hey [name] — on a job, give me [time]. 🤙"
        const val DEFAULT_CONTACT_MESSAGE = "Hey [name], I'm with a client. I'll call you back within the hour."

        // After-hours message key
        private val KEY_AFTER_HOURS_MESSAGE = stringPreferencesKey("after_hours_message")

        // Google Review URL key
        private val KEY_GOOGLE_REVIEW_URL = stringPreferencesKey("google_review_url")

        // Trade job value map
        val TRADE_JOB_VALUES: Map<String, Int> = mapOf(
            "plumber" to 850,
            "hvac" to 1200,
            "roofer" to 4500,
            "electrician" to 900,
            "general_contractor" to 2800,
            "painter" to 600,
            "landscaper" to 400,
            "other" to 750
        )

        // Business profile keys (used by AI conversation feature)
        private val KEY_BUSINESS_NAME = stringPreferencesKey("business_name")
        private val KEY_OWNER_NAME = stringPreferencesKey("owner_name")
        private val KEY_TRADE_TYPE = stringPreferencesKey("trade_type")
        private val KEY_SERVICE_AREA = stringPreferencesKey("service_area")
        private val KEY_CALLBACK_WINDOW = stringPreferencesKey("callback_window")
        private val KEY_PRICING_INFO = stringPreferencesKey("pricing_info")
        private val KEY_AI_CONVERSATION_ENABLED = booleanPreferencesKey("ai_conversation_enabled")

        // Default values
        const val DEFAULT_MESSAGE = "Hi! I'm on a job right now and can't answer. I'll call you right back—what is your name and what do you need help with?"
        const val DEFAULT_START_HOUR = 8
        const val DEFAULT_START_MINUTE = 0
        const val DEFAULT_END_HOUR = 18
        const val DEFAULT_END_MINUTE = 0
        const val FREE_TIER_MONTHLY_LIMIT = 10
        // PRO and above are unlimited — no monthly cap above FREE
        const val DEBOUNCE_WINDOW_MS = 30 * 60 * 1000L // 30 minutes
    }

    // Flows for reactive UI updates
    val isEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_ENABLED] ?: false }
    val message: Flow<String> = context.dataStore.data.map { it[KEY_MESSAGE] ?: DEFAULT_MESSAGE }
    val startHour: Flow<Int> = context.dataStore.data.map { it[KEY_START_HOUR] ?: DEFAULT_START_HOUR }
    val startMinute: Flow<Int> = context.dataStore.data.map { it[KEY_START_MINUTE] ?: DEFAULT_START_MINUTE }
    val endHour: Flow<Int> = context.dataStore.data.map { it[KEY_END_HOUR] ?: DEFAULT_END_HOUR }
    val endMinute: Flow<Int> = context.dataStore.data.map { it[KEY_END_MINUTE] ?: DEFAULT_END_MINUTE }
    val isAlwaysOn: Flow<Boolean> = context.dataStore.data.map { it[KEY_ALWAYS_ON] ?: false }
    val autoTextCount: Flow<Int> = context.dataStore.data.map { it[KEY_AUTO_TEXT_COUNT] ?: 0 }
    val isUnlimited: Flow<Boolean> = context.dataStore.data.map { it[KEY_IS_UNLIMITED] ?: false }
    val pausedUntil: Flow<Long> = context.dataStore.data.map { it[KEY_PAUSED_UNTIL] ?: 0L }
    val isOnboarded: Flow<Boolean> = context.dataStore.data.map { it[KEY_ONBOARDED] ?: false }
    val replyToContactsOnly: Flow<Boolean> = context.dataStore.data.map { it[KEY_REPLY_TO_CONTACTS_ONLY] ?: false }
    val trialStartedAt: Flow<Long> = context.dataStore.data.map { it[KEY_TRIAL_STARTED_AT] ?: 0L }
    val isMasterGodMode: Flow<Boolean> = context.dataStore.data.map { it[KEY_IS_MASTER_GOD_MODE] ?: false }
    val humanDelayEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_HUMAN_DELAY_ENABLED] ?: true }
    val humanDelaySeconds: Flow<Int> = context.dataStore.data.map { it[KEY_HUMAN_DELAY_SECONDS] ?: 30 }
    val subscriptionTier: Flow<SubscriptionTier> = context.dataStore.data.map { 
        SubscriptionTier.fromId(it[KEY_SUBSCRIPTION_TIER] ?: SubscriptionTier.FREE.id) 
    }

    // Combined flow for usage status
    val usageStatus: Flow<UsageStatus> = context.dataStore.data.map { prefs ->
        val count = prefs[KEY_AUTO_TEXT_COUNT] ?: 0
        val tierId = prefs[KEY_SUBSCRIPTION_TIER] ?: SubscriptionTier.FREE.id
        val tier = SubscriptionTier.fromId(tierId)
        val unlimited = tier.isUnlimited || (prefs[KEY_IS_UNLIMITED] ?: false)
        UsageStatus(count, unlimited, tier)
    }

    // Suspend functions for write operations
    suspend fun setEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_ENABLED] = enabled }
    }

    suspend fun setMessage(message: String) {
        context.dataStore.edit { it[KEY_MESSAGE] = message }
    }

    suspend fun resetMessageToDefault() {
        context.dataStore.edit { it[KEY_MESSAGE] = DEFAULT_MESSAGE }
    }

    suspend fun setActiveHours(startHour: Int, startMinute: Int, endHour: Int, endMinute: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_START_HOUR] = startHour
            prefs[KEY_START_MINUTE] = startMinute
            prefs[KEY_END_HOUR] = endHour
            prefs[KEY_END_MINUTE] = endMinute
        }
    }

    suspend fun setAlwaysOn(alwaysOn: Boolean) {
        context.dataStore.edit { it[KEY_ALWAYS_ON] = alwaysOn }
    }

    suspend fun incrementAutoTextCount() {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_AUTO_TEXT_COUNT] ?: 0
            prefs[KEY_AUTO_TEXT_COUNT] = current + 1
        }
    }

    suspend fun setUnlimited(unlimited: Boolean) {
        context.dataStore.edit { it[KEY_IS_UNLIMITED] = unlimited }
    }

    suspend fun setSubscriptionTier(tier: SubscriptionTier) {
        context.dataStore.edit { it[KEY_SUBSCRIPTION_TIER] = tier.id }
    }

    suspend fun pauseForOneHour() {
        val pauseUntil = System.currentTimeMillis() + (60 * 60 * 1000) // 1 hour from now
        context.dataStore.edit { it[KEY_PAUSED_UNTIL] = pauseUntil }
    }

    suspend fun clearPause() {
        context.dataStore.edit { it[KEY_PAUSED_UNTIL] = 0L }
    }

    suspend fun setOnboarded(onboarded: Boolean) {
        val now = System.currentTimeMillis()
        context.dataStore.edit { prefs ->
            prefs[KEY_ONBOARDED] = onboarded
            if ((prefs[KEY_TRIAL_STARTED_AT] ?: 0L) == 0L) {
                prefs[KEY_TRIAL_STARTED_AT] = now
                prefs[KEY_LAST_RESET_AT] = now
            }
        }
    }

    suspend fun setMasterGodMode(unlocked: Boolean) {
        context.dataStore.edit { it[KEY_IS_MASTER_GOD_MODE] = unlocked }
    }

    /** Resets the monthly auto-reply counter once 30 days have elapsed since the last reset. */
    suspend fun checkAndResetMonthlyUsage() {
        val lastReset = context.dataStore.data.map { it[KEY_LAST_RESET_AT] ?: 0L }.first()
        val thirtyDaysMs = 30L * 24 * 60 * 60 * 1000
        if (System.currentTimeMillis() - lastReset > thirtyDaysMs) {
            context.dataStore.edit {
                it[KEY_AUTO_TEXT_COUNT] = 0
                it[KEY_LAST_RESET_AT] = System.currentTimeMillis()
            }
        }
    }

    // Kept for backward compat — callers should migrate to checkAndResetMonthlyUsage
    @Deprecated("Use checkAndResetMonthlyUsage", ReplaceWith("checkAndResetMonthlyUsage()"))
    suspend fun checkAndResetWeeklyUsage() = checkAndResetMonthlyUsage()

    suspend fun setReplyToContactsOnly(enabled: Boolean) {
        context.dataStore.edit { it[KEY_REPLY_TO_CONTACTS_ONLY] = enabled }
    }

    suspend fun isPaused(): Boolean {
        val pausedUntil = context.dataStore.data.map { it[KEY_PAUSED_UNTIL] ?: 0L }.first()
        return System.currentTimeMillis() < pausedUntil
    }

    // Synchronous read helpers for background service/receiver
    suspend fun isEnabledSync(): Boolean {
        return context.dataStore.data.map { it[KEY_ENABLED] ?: false }.first()
    }

    suspend fun getMessageSync(): String {
        return context.dataStore.data.map { it[KEY_MESSAGE] ?: DEFAULT_MESSAGE }.first()
    }

    suspend fun isAlwaysOnSync(): Boolean {
        return context.dataStore.data.map { it[KEY_ALWAYS_ON] ?: false }.first()
    }

    suspend fun isUnlimitedSync(): Boolean {
        return context.dataStore.data.map { it[KEY_IS_UNLIMITED] ?: false }.first()
    }

    suspend fun getAutoTextCountSync(): Int {
        return context.dataStore.data.map { it[KEY_AUTO_TEXT_COUNT] ?: 0 }.first()
    }

    suspend fun isReplyToContactsOnlySync(): Boolean {
        return context.dataStore.data.map { it[KEY_REPLY_TO_CONTACTS_ONLY] ?: false }.first()
    }

    suspend fun isMasterGodModeSync(): Boolean {
        return context.dataStore.data.map { it[KEY_IS_MASTER_GOD_MODE] ?: false }.first()
    }

    suspend fun isHumanDelayEnabledSync(): Boolean {
        return context.dataStore.data.map { it[KEY_HUMAN_DELAY_ENABLED] ?: true }.first()
    }

    suspend fun getHumanDelaySecondsSync(): Int {
        return context.dataStore.data.map { it[KEY_HUMAN_DELAY_SECONDS] ?: 30 }.first()
    }

    suspend fun getSubscriptionTierSync(): SubscriptionTier {
        return context.dataStore.data.map { 
            SubscriptionTier.fromId(it[KEY_SUBSCRIPTION_TIER] ?: SubscriptionTier.FREE.id) 
        }.first()
    }

    suspend fun setHumanDelay(enabled: Boolean, seconds: Int) {
        context.dataStore.edit { 
            it[KEY_HUMAN_DELAY_ENABLED] = enabled
            it[KEY_HUMAN_DELAY_SECONDS] = seconds
        }
    }

    suspend fun isTrialExpiredSync(): Boolean {
        if (isMasterGodModeSync()) return false
        val startedAt = context.dataStore.data.map { it[KEY_TRIAL_STARTED_AT] ?: 0L }.first()
        if (startedAt == 0L) return false
        val sevenDaysMs = 7L * 24 * 60 * 60 * 1000
        return System.currentTimeMillis() - startedAt > sevenDaysMs
    }

    /**
     * Check if we've reached the free tier limit.
     */
    suspend fun hasReachedLimit(): Boolean {
        if (isMasterGodModeSync()) return false
        
        val tier = getSubscriptionTierSync()
        if (tier.isUnlimited) return false
        
        val count = getAutoTextCountSync()
        return count >= tier.monthlyLimit
    }

    /**
     * Check if current time is within active hours.
     */
    suspend fun isWithinActiveHours(): Boolean {
        if (isAlwaysOnSync()) return true

        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val currentTimeInMinutes = currentHour * 60 + currentMinute

        val prefs = context.dataStore.data.first()
        val startH = prefs[KEY_START_HOUR] ?: DEFAULT_START_HOUR
        val startM = prefs[KEY_START_MINUTE] ?: DEFAULT_START_MINUTE
        val endH = prefs[KEY_END_HOUR] ?: DEFAULT_END_HOUR
        val endM = prefs[KEY_END_MINUTE] ?: DEFAULT_END_MINUTE
        val start = startH * 60 + startM
        val end = endH * 60 + endM

        return if (start < end) {
            // Normal case: e.g., 8:00 AM to 6:00 PM
            currentTimeInMinutes in start until end
        } else {
            // Crosses midnight: e.g., 10:00 PM to 2:00 AM
            currentTimeInMinutes >= start || currentTimeInMinutes < end
        }
    }

    /**
     * Debounce tracking - store last text time per phone number.
     */
    suspend fun canSendToNumber(phoneNumber: String): Boolean {
        val lastTextTime = getLastTextTime(phoneNumber)
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastTextTime) > DEBOUNCE_WINDOW_MS
    }

    suspend fun recordTextSent(phoneNumber: String) {
        val key = longPreferencesKey("last_text_${phoneNumber.filter { it.isDigit() }}")
        context.dataStore.edit { it[key] = System.currentTimeMillis() }
    }

    private suspend fun getLastTextTime(phoneNumber: String): Long {
        val key = longPreferencesKey("last_text_${phoneNumber.filter { it.isDigit() }}")
        return context.dataStore.data.map { it[key] ?: 0L }.first()
    }

    // ── Message Templates (Operator tier) ────────────────────────────────────

    val vipMessage: Flow<String> = context.dataStore.data.map { it[KEY_VIP_MESSAGE] ?: DEFAULT_VIP_MESSAGE }
    val contactMessage: Flow<String> = context.dataStore.data.map { it[KEY_CONTACT_MESSAGE] ?: DEFAULT_CONTACT_MESSAGE }
    val leadMessage: Flow<String> = context.dataStore.data.map { it[KEY_LEAD_MESSAGE] ?: DEFAULT_MESSAGE }

    suspend fun setVipMessage(msg: String) {
        context.dataStore.edit { it[KEY_VIP_MESSAGE] = msg }
    }

    suspend fun setContactMessage(msg: String) {
        context.dataStore.edit { it[KEY_CONTACT_MESSAGE] = msg }
    }

    suspend fun setLeadMessage(msg: String) {
        context.dataStore.edit { it[KEY_LEAD_MESSAGE] = msg }
    }

    suspend fun getVipMessageSync(): String =
        context.dataStore.data.map { it[KEY_VIP_MESSAGE] ?: DEFAULT_VIP_MESSAGE }.first()

    suspend fun getContactMessageSync(): String =
        context.dataStore.data.map { it[KEY_CONTACT_MESSAGE] ?: DEFAULT_CONTACT_MESSAGE }.first()

    suspend fun getLeadMessageSync(): String =
        context.dataStore.data.map { it[KEY_LEAD_MESSAGE] ?: DEFAULT_MESSAGE }.first()

    // ── Voice Greeting (Voice tier) ───────────────────────────────────────────

    val voiceGreeting: Flow<String> = context.dataStore.data.map { it[KEY_VOICE_GREETING] ?: "" }

    suspend fun setVoiceGreeting(script: String) {
        context.dataStore.edit { it[KEY_VOICE_GREETING] = script }
    }

    // ── Supabase Auth ─────────────────────────────────────────────────────────

    val supabaseToken: Flow<String> = context.dataStore.data.map { it[KEY_SUPABASE_TOKEN] ?: "" }
    val supabaseUserId: Flow<String> = context.dataStore.data.map { it[KEY_SUPABASE_USER_ID] ?: "" }
    val crmEmail: Flow<String> = context.dataStore.data.map { it[KEY_CRM_EMAIL] ?: "" }

    suspend fun saveSupabaseSession(token: String, userId: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SUPABASE_TOKEN] = token
            prefs[KEY_SUPABASE_USER_ID] = userId
            prefs[KEY_CRM_EMAIL] = email
        }
    }

    suspend fun clearSupabaseSession() {
        context.dataStore.edit { prefs ->
            prefs[KEY_SUPABASE_TOKEN] = ""
            prefs[KEY_SUPABASE_USER_ID] = ""
            prefs[KEY_CRM_EMAIL] = ""
        }
    }

    suspend fun getSupabaseTokenSync(): String =
        context.dataStore.data.map { it[KEY_SUPABASE_TOKEN] ?: "" }.first()

    suspend fun getSupabaseUserIdSync(): String =
        context.dataStore.data.map { it[KEY_SUPABASE_USER_ID] ?: "" }.first()

    // ── Business Profile (Operator tier AI features) ─────────────────────────

    val businessName: Flow<String> = context.dataStore.data.map { it[KEY_BUSINESS_NAME] ?: "" }
    val ownerName: Flow<String> = context.dataStore.data.map { it[KEY_OWNER_NAME] ?: "" }
    val tradeType: Flow<String> = context.dataStore.data.map { it[KEY_TRADE_TYPE] ?: "" }
    val serviceArea: Flow<String> = context.dataStore.data.map { it[KEY_SERVICE_AREA] ?: "" }
    val callbackWindow: Flow<String> = context.dataStore.data.map { it[KEY_CALLBACK_WINDOW] ?: "within 1 hour" }
    val pricingInfo: Flow<String> = context.dataStore.data.map { it[KEY_PRICING_INFO] ?: "" }
    val aiConversationEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_AI_CONVERSATION_ENABLED] ?: true }

    /** Computed flow: estimated average job value in dollars based on trade type. */
    val tradeJobValue: Flow<Int> = context.dataStore.data.map { prefs ->
        val trade = (prefs[KEY_TRADE_TYPE] ?: "").lowercase().trim()
        TRADE_JOB_VALUES[trade] ?: TRADE_JOB_VALUES["other"] ?: 750
    }

    // After-hours message
    val afterHoursMessage: Flow<String> = context.dataStore.data.map {
        it[KEY_AFTER_HOURS_MESSAGE] ?: ""
    }

    suspend fun setAfterHoursMessage(message: String) {
        context.dataStore.edit { it[KEY_AFTER_HOURS_MESSAGE] = message }
    }

    suspend fun getAfterHoursMessageSync(): String =
        context.dataStore.data.map { it[KEY_AFTER_HOURS_MESSAGE] ?: "" }.first()

    // Google Review URL
    val googleReviewUrl: Flow<String> = context.dataStore.data.map { it[KEY_GOOGLE_REVIEW_URL] ?: "" }

    suspend fun setGoogleReviewUrl(url: String) {
        context.dataStore.edit { it[KEY_GOOGLE_REVIEW_URL] = url }
    }

    suspend fun saveBusinessProfile(
        businessName: String,
        ownerName: String,
        tradeType: String,
        serviceArea: String,
        callbackWindow: String,
        pricingInfo: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_BUSINESS_NAME] = businessName
            prefs[KEY_OWNER_NAME] = ownerName
            prefs[KEY_TRADE_TYPE] = tradeType
            prefs[KEY_SERVICE_AREA] = serviceArea
            prefs[KEY_CALLBACK_WINDOW] = callbackWindow
            prefs[KEY_PRICING_INFO] = pricingInfo
        }
    }

    suspend fun setAiConversationEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_AI_CONVERSATION_ENABLED] = enabled }
    }

    suspend fun getBusinessProfileSync(): BusinessProfile {
        val prefs = context.dataStore.data.first()
        return BusinessProfile(
            businessName = prefs[KEY_BUSINESS_NAME] ?: "",
            ownerName = prefs[KEY_OWNER_NAME] ?: "",
            tradeType = prefs[KEY_TRADE_TYPE] ?: "",
            serviceArea = prefs[KEY_SERVICE_AREA] ?: "",
            callbackWindow = prefs[KEY_CALLBACK_WINDOW] ?: "within 1 hour",
            pricingInfo = prefs[KEY_PRICING_INFO] ?: ""
        )
    }
}

/**
 * Data class representing usage status.
 */
data class UsageStatus(
    val count: Int,
    val isUnlimited: Boolean,
    val tier: SubscriptionTier = SubscriptionTier.FREE
) {
    val hasReachedLimit: Boolean
        get() = !isUnlimited && count >= tier.monthlyLimit
}

/**
 * Business profile used by the AI conversation feature (Operator tier).
 */
data class BusinessProfile(
    val businessName: String,
    val ownerName: String,
    val tradeType: String,
    val serviceArea: String,
    val callbackWindow: String,
    val pricingInfo: String
) {
    val isComplete: Boolean
        get() = businessName.isNotBlank() && ownerName.isNotBlank() && tradeType.isNotBlank()
}
