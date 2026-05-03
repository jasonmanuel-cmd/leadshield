package com.mctb.autoreply.util

object PhoneNumberValidator {
    private val invalidPatterns = listOf(
        "-1",
        "-2",
        "0",
        "UNKNOWN",
        "PRIVATE",
        "ANONYMOUS",
        "BLOCKED",
        "RESTRICTED"
    )

    fun isValid(phoneNumber: String): Boolean {
        if (phoneNumber.isBlank()) return false

        val cleanNumber = phoneNumber.uppercase().trim()
        if (invalidPatterns.any { cleanNumber.contains(it) }) {
            return false
        }

        return phoneNumber.count { it.isDigit() } >= 3
    }
}
