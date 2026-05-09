package com.leadshield.app.data

/**
 * Data class representing the current subscription state of the user.
 */
data class SubscriptionStatus(
    val tier: SubscriptionTier = SubscriptionTier.FREE,
    val expiryTimestamp: Long = 0L,
    val isAutoRenewEnabled: Boolean = false,
    val purchaseToken: String? = null,
    val productId: String? = null
) {
    val isProOrAbove: Boolean
        get() = tier != SubscriptionTier.FREE

    val isOperatorOrAbove: Boolean
        get() = tier == SubscriptionTier.OPERATOR || tier == SubscriptionTier.MASTER

    val isSubscriptionActive: Boolean
        get() = tier == SubscriptionTier.MASTER || expiryTimestamp > System.currentTimeMillis()
}
