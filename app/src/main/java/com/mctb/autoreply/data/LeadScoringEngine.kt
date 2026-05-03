package com.mctb.autoreply.data

object LeadScoringEngine {
    private val HOT_KEYWORDS = listOf(
        "urgent", "emergency", "asap", "today", "now", "flooding", "leaking",
        "no heat", "broken", "burst", "help", "immediately", "right away", "desperate"
    )
    private val WARM_KEYWORDS = listOf(
        "quote", "estimate", "available", "schedule", "appointment", "next week",
        "how much", "price", "cost", "interested", "address"
    )

    fun scoreMessage(message: String): Int {
        val lower = message.lowercase()
        return when {
            HOT_KEYWORDS.any { lower.contains(it) } -> 2   // HOT
            WARM_KEYWORDS.any { lower.contains(it) } -> 1  // WARM
            else -> 0                                        // COLD
        }
    }

    fun scoreLabel(score: Int) = when (score) {
        2 -> "🔴 HOT"
        1 -> "🟡 WARM"
        else -> "⚪ COLD"
    }

    fun scoreColor(score: Int) = score // caller maps to NeonCyan/NeonGold/TextSecondary
}
