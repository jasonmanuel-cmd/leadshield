package com.leadshield.app.ui.navigation

/**
 * Navigation routes for the app.
 */
sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object MessageEditor : Screen("message_editor")
    object ActiveHours : Screen("active_hours")
    object Usage : Screen("usage")
    object Settings : Screen("settings")
    object History : Screen("history")
    object SovereignDiary : Screen("sovereign_diary")
    object WeeklyAudit : Screen("weekly_audit")
    object Subscription : Screen("subscription")
    object ContactOverrides : Screen("contact_overrides")
    object ContactEditor : Screen("contact_editor/{phoneNumber}") {
        fun createRoute(phoneNumber: String) = "contact_editor/$phoneNumber"
    }
    object GodMode : Screen("god_mode")
    object BusinessProfile : Screen("business_profile")
    object VipContacts : Screen("vip_contacts")
    object MessageTemplates : Screen("message_templates")
    object Booking : Screen("booking/{phoneNumber}") {
        fun createRoute(phoneNumber: String) = "booking/$phoneNumber"
    }
    object VoiceSetup : Screen("voice_setup")
}
