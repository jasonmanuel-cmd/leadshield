package com.mctb.autoreply.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * High-level manager that coordinates between local [AppPreferences] 
 * and remote [BillingManager] state.
 */
class SubscriptionManager(
    private val context: Context,
    private val prefs: AppPreferences,
    private val billingManager: BillingManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    /**
     * Combined flow of the current subscription status.
     * Priority (highest wins): MASTER(BuildConfig==5 or isMaster) > TEAM > VOICE > OPERATOR > PRO > FREE
     */
    val subscriptionStatus: Flow<SubscriptionStatus> = combine(
        combine(
            prefs.subscriptionTier,
            billingManager.isPro,
            billingManager.isOperator
        ) { localTier, isPro, isOperator -> Triple(localTier, isPro, isOperator) },
        combine(
            billingManager.isMaster,
            billingManager.isVoice,
            billingManager.isTeam
        ) { isMaster, isVoice, isTeam -> Triple(isMaster, isVoice, isTeam) }
    ) { (localTier, isPro, isOperator), (isMaster, isVoice, isTeam) ->
        val effectiveTier = when {
            com.mctb.autoreply.BuildConfig.SUBSCRIPTION_TIER == 5 -> SubscriptionTier.TEAM
            com.mctb.autoreply.BuildConfig.SUBSCRIPTION_TIER == 4 -> SubscriptionTier.VOICE
            com.mctb.autoreply.BuildConfig.SUBSCRIPTION_TIER == 3 -> SubscriptionTier.MASTER
            isMaster -> SubscriptionTier.MASTER
            isTeam -> SubscriptionTier.TEAM
            isVoice -> SubscriptionTier.VOICE
            isOperator -> SubscriptionTier.OPERATOR
            isPro -> SubscriptionTier.PRO
            else -> localTier
        }

        SubscriptionStatus(
            tier = effectiveTier,
            expiryTimestamp = if (effectiveTier != SubscriptionTier.FREE) Long.MAX_VALUE else 0L
        )
    }

    init {
        // Hard-set tier for flavor builds
        when (com.mctb.autoreply.BuildConfig.SUBSCRIPTION_TIER) {
            3 -> scope.launch { prefs.setSubscriptionTier(SubscriptionTier.MASTER) }
            4 -> scope.launch { prefs.setSubscriptionTier(SubscriptionTier.VOICE) }
            5 -> scope.launch { prefs.setSubscriptionTier(SubscriptionTier.TEAM) }
        }

        // Sync billing state → DataStore for offline access (service/receiver)
        scope.launch {
            billingManager.isPro.collect { isPro ->
                if (isPro && prefs.getSubscriptionTierSync() == SubscriptionTier.FREE) {
                    prefs.setSubscriptionTier(SubscriptionTier.PRO)
                }
            }
        }

        scope.launch {
            billingManager.isOperator.collect { isOperator ->
                if (isOperator && prefs.getSubscriptionTierSync().id < SubscriptionTier.OPERATOR.id) {
                    prefs.setSubscriptionTier(SubscriptionTier.OPERATOR)
                }
            }
        }

        scope.launch {
            billingManager.isMaster.collect { isMaster ->
                if (isMaster) prefs.setSubscriptionTier(SubscriptionTier.MASTER)
            }
        }

        scope.launch {
            billingManager.isVoice.collect { isVoice ->
                if (isVoice && prefs.getSubscriptionTierSync().id < SubscriptionTier.VOICE.id) {
                    prefs.setSubscriptionTier(SubscriptionTier.VOICE)
                }
            }
        }

        scope.launch {
            billingManager.isTeam.collect { isTeam ->
                if (isTeam && prefs.getSubscriptionTierSync().id < SubscriptionTier.TEAM.id) {
                    prefs.setSubscriptionTier(SubscriptionTier.TEAM)
                }
            }
        }
    }

    /**
     * Feature gate check for the current subscription tier.
     */
    suspend fun canUseFeature(feature: Feature): Boolean {
        val tier = subscriptionStatus.first().tier
        return when (feature) {
            Feature.BASIC_AUTO_REPLY -> true
            Feature.HUMAN_DELAY -> tier.id >= SubscriptionTier.PRO.id
            Feature.UNLIMITED_REPLIES -> tier.isUnlimited
            Feature.AI_CONVERSATION -> tier.hasAiConversation
            Feature.CUSTOM_CONTACTS -> tier.id >= SubscriptionTier.PRO.id
        }
    }

    enum class Feature {
        BASIC_AUTO_REPLY,
        HUMAN_DELAY,
        UNLIMITED_REPLIES,
        AI_CONVERSATION,
        CUSTOM_CONTACTS
    }
}
