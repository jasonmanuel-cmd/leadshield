package com.mctb.autoreply.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mctb.autoreply.ui.screens.*
import com.mctb.autoreply.ui.theme.AppTheme
import com.mctb.autoreply.data.*
import com.mctb.autoreply.ui.navigation.Screen
import com.mctb.autoreply.service.CipherService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main activity for the app.
 * Sets up Jetpack Compose navigation and theming.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var prefs: AppPreferences

    @Inject
    lateinit var auditRepository: AuditRepository

    @Inject
    lateinit var contactRepository: ContactRepository

    @Inject
    lateinit var billingManager: BillingManager

    @Inject
    lateinit var subscriptionManager: SubscriptionManager

    @Inject
    lateinit var cipherService: CipherService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val isOnboarded by prefs.isOnboarded.collectAsState(initial = null)

                    if (isOnboarded != null) {
                        val navController = rememberNavController()

                        NavHost(
                            navController = navController,
                            startDestination = if (isOnboarded == true) Screen.Home.route else Screen.Onboarding.route
                        ) {
                            composable(Screen.Onboarding.route) {
                                OnboardingScreen(navController)
                            }
                            composable(Screen.Home.route) {
                                HomeScreen(navController, prefs, subscriptionManager)
                            }
                            composable(Screen.MessageEditor.route) {
                                MessageEditorScreen(navController)
                            }
                            composable(Screen.ActiveHours.route) {
                                ActiveHoursScreen(navController)
                            }
                            composable(Screen.Usage.route) {
                                UsageScreen(navController)
                            }
                            composable(Screen.Settings.route) {
                                SettingsScreen(navController)
                            }
                            composable(Screen.History.route) {
                                HistoryScreen(navController)
                            }
                            composable(Screen.SovereignDiary.route) {
                                MasterGatedScreen(navController, prefs) {
                                    SovereignDiaryScreen(cipherService, auditRepository)
                                }
                            }
                            composable(Screen.WeeklyAudit.route) {
                                MasterGatedScreen(navController, prefs) {
                                    WeeklyAuditDashboard(auditRepository)
                                }
                            }
                            composable(Screen.Subscription.route) {
                                SubscriptionScreen(navController, subscriptionManager, billingManager)
                            }
                            composable(Screen.ContactOverrides.route) {
                                ContactOverridesScreen(navController, contactRepository)
                            }
                            composable(Screen.ContactEditor.route) { backStackEntry ->
                                val phone = backStackEntry.arguments?.getString("phoneNumber") ?: "new"
                                ContactEditorScreen(navController, phone, contactRepository)
                            }
                            composable(Screen.GodMode.route) {
                                MasterGatedScreen(navController, prefs) {
                                    GodModeScreen(navController)
                                }
                            }
                            composable(Screen.BusinessProfile.route) {
                                BusinessProfileScreen(navController, prefs)
                            }
                            composable(Screen.VipContacts.route) {
                                VipContactsScreen(navController)
                            }
                            composable(Screen.MessageTemplates.route) {
                                MessageTemplatesScreen(navController)
                            }
                            composable(Screen.Booking.route) { backStackEntry ->
                                val phone = backStackEntry.arguments?.getString("phoneNumber") ?: ""
                                BookingScreen(navController, phone)
                            }
                            composable(Screen.VoiceSetup.route) {
                                VoiceSetupScreen(navController, prefs)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * A wrapper for screens that require a MASTER tier subscription.
 */
@Composable
fun MasterGatedScreen(
    navController: androidx.navigation.NavController,
    prefs: AppPreferences,
    content: @Composable () -> Unit
) {
    val tier by prefs.subscriptionTier.collectAsState(initial = SubscriptionTier.FREE)
    val isGodMode by prefs.isMasterGodMode.collectAsState(initial = false)

    if (tier == SubscriptionTier.MASTER || isGodMode) {
        content()
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "MASTER TIER REQUIRED",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Unlock the Sovereign Diary and System Audit by upgrading to the Master Tier.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { navController.navigate(Screen.Subscription.route) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0))
                ) {
                    Text("UPGRADE TO MASTER", fontWeight = FontWeight.Bold)
                }
                
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("GO BACK", color = Color.Gray)
                }
            }
        }
    }
}
