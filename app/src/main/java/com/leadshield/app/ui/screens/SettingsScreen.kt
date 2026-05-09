package com.leadshield.app.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.leadshield.app.BuildConfig
import com.leadshield.app.R
import com.leadshield.app.data.AppPreferences
import com.leadshield.app.data.SubscriptionTier
import com.leadshield.app.ui.navigation.Screen
import com.leadshield.app.ui.theme.NeonGold
import com.leadshield.app.ui.components.GlassCard
import com.leadshield.app.ui.components.AnimatedMeshBackground
import com.leadshield.app.ui.theme.NeonCyan
import com.leadshield.app.ui.theme.SpaceBlack
import com.leadshield.app.ui.theme.TextPrimary
import com.leadshield.app.ui.theme.TextSecondary
import com.leadshield.app.ui.theme.NeonPurple
import androidx.compose.ui.graphics.Brush
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = remember { AppPreferences(context) }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedMeshBackground()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(NeonCyan, NeonPurple)
                                        ),
                                        shape = CircleShape
                                    )
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Settings",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.05f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back),
                                tint = TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = TextPrimary
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Plan & Subscription Section
                Text(
                    text = "PLAN & SUBSCRIPTION",
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )

                val currentTier by prefs.subscriptionTier.collectAsState(initial = SubscriptionTier.FREE)

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        val (planLabel, planDesc, planColor) = when (currentTier) {
                            SubscriptionTier.FREE -> Triple(
                                "Current plan: Free (10 auto-replies / month)",
                                "Good for testing. When you're ready to stop losing jobs, upgrade.",
                                TextPrimary
                            )
                            SubscriptionTier.PRO -> Triple(
                                "Current plan: Pro (25 auto-replies / month)",
                                "No ads, reply delay, priority support.",
                                NeonCyan
                            )
                            SubscriptionTier.OPERATOR, SubscriptionTier.MASTER -> Triple(
                                "Current plan: Operator (unlimited + AI)",
                                "Your AI receptionist is active. Every missed call gets handled.",
                                NeonGold
                            )
                            SubscriptionTier.VOICE -> Triple(
                                "Current plan: Voice ($99/mo)",
                                "AI voice answering — your phone is always answered.",
                                NeonPurple
                            )
                            SubscriptionTier.TEAM -> Triple(
                                "Current plan: Team ($129/mo)",
                                "3-phone team mode with shared lead pipeline.",
                                NeonCyan
                            )
                        }
                        Text(planLabel, style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold, color = planColor)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(planDesc, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        if (currentTier == SubscriptionTier.FREE || currentTier == SubscriptionTier.PRO ||
                            currentTier == SubscriptionTier.OPERATOR) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { navController.navigate(Screen.Subscription.route) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentTier == SubscriptionTier.FREE) NeonCyan else NeonGold
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (currentTier == SubscriptionTier.FREE) "Upgrade to Pro or Operator"
                                           else "Upgrade to Operator — AI features",
                                    color = SpaceBlack,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }

                // AI Profile card — only shown for Operator/Master/Voice/Team users
                if (currentTier == SubscriptionTier.OPERATOR || currentTier == SubscriptionTier.MASTER ||
                    currentTier == SubscriptionTier.VOICE || currentTier == SubscriptionTier.TEAM) {
                    Spacer(modifier = Modifier.height(4.dp))
                    SettingsCard(
                        icon = Icons.Default.Bolt,
                        title = "AI Receptionist Profile",
                        description = "Set your business info so the AI can answer callers",
                        onClick = { navController.navigate(Screen.BusinessProfile.route) }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    SettingsCard(
                        icon = Icons.Default.Star,
                        title = "VIP Contacts",
                        description = "Send special replies to your most important callers",
                        onClick = { navController.navigate(Screen.VipContacts.route) }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    SettingsCard(
                        icon = Icons.Default.Message,
                        title = "Message Templates",
                        description = "Manage auto-reply templates by contact type",
                        onClick = { navController.navigate(Screen.MessageTemplates.route) }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    SettingsCard(
                        icon = Icons.Default.PhoneInTalk,
                        title = "AI Voice Setup",
                        description = if (currentTier == SubscriptionTier.VOICE || currentTier == SubscriptionTier.TEAM || currentTier == SubscriptionTier.MASTER)
                            "Configure your AI phone answering (launching soon)"
                        else
                            "Configure your AI phone answering — coming to your plan",
                        onClick = { navController.navigate(Screen.VoiceSetup.route) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quality & Spam Section
                Text(
                    text = "AUTO-REPLY RULES",
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )

                val contactsOnly by prefs.replyToContactsOnly.collectAsState(initial = false)

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.contacts_only),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                text = stringResource(R.string.contacts_only_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                        Switch(
                            checked = contactsOnly,
                            onCheckedChange = { 
                                scope.launch { prefs.setReplyToContactsOnly(it) }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NeonCyan,
                                checkedTrackColor = NeonCyan.copy(alpha = 0.5f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val humanDelayEnabled by prefs.humanDelayEnabled.collectAsState(initial = true)
                val delaySec by prefs.humanDelaySeconds.collectAsState(initial = 30)

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Reply Delay",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                text = "Delay each reply slightly to feel natural",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        Switch(
                            checked = humanDelayEnabled,
                            onCheckedChange = { 
                                scope.launch { prefs.setHumanDelay(it, delaySec) }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NeonCyan,
                                checkedTrackColor = NeonCyan.copy(alpha = 0.5f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Settings Section Title
                Text(
                    text = "PHONE & SYSTEM",
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )

                // Battery Optimization Card
                SettingsCard(
                    icon = Icons.Default.BatteryChargingFull,
                    title = stringResource(R.string.battery_optimization_title),
                    description = stringResource(R.string.battery_optimization_desc),
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            // Open the per-app optimization screen directly so the user
                            // can whitelist this app with one tap, not search through a list.
                            val intent = Intent(
                                Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                                android.net.Uri.parse("package:${context.packageName}")
                            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        }
                    }
                )

                // Notifications Card
                SettingsCard(
                    icon = Icons.Default.Notifications,
                    title = "Notification Settings",
                    description = "Manage app notifications and channels",
                    onClick = {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // About Section Title
                Text(
                    text = "APP INFO",
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )

                // SECRET UNLOCK LOGIC
                val isMasterUnlocked by prefs.isMasterGodMode.collectAsState(initial = false)
                var versionTapCount by remember { mutableStateOf(0) }
                var showPasswordDialog by remember { mutableStateOf(false) }
                var passwordInput by remember { mutableStateOf("") }

                // App Info Card with Secret Tap
                SettingsCard(
                    icon = if (isMasterUnlocked) Icons.Default.Bolt else Icons.Default.Info,
                    title = if (isMasterUnlocked) "GOD MODE ACTIVE" else "App Version",
                    description = if (isMasterUnlocked) "Infinite Licenses - Admin Version"
                                  else "${BuildConfig.VERSION_NAME} (Build ${BuildConfig.VERSION_CODE})",
                    onClick = { 
                        if (!isMasterUnlocked) {
                            versionTapCount++
                            if (versionTapCount >= 7) {
                                showPasswordDialog = true
                                versionTapCount = 0
                            }
                        }
                    }
                )

                if (showPasswordDialog) {
                    androidx.compose.ui.window.Dialog(onDismissRequest = { showPasswordDialog = false }) {
                        GlassCard(cornerRadius = 24.dp) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Text("Enter Master Password", fontWeight = FontWeight.Bold, color = NeonCyan)
                                Spacer(modifier = Modifier.height(16.dp))
                                TextField(
                                    value = passwordInput,
                                    onValueChange = { passwordInput = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = SpaceBlack.copy(alpha = 0.5f),
                                        unfocusedContainerColor = SpaceBlack.copy(alpha = 0.3f),
                                        focusedTextColor = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = {
                                        val masterPassword = BuildConfig.MASTER_GOD_MODE_PASSWORD
                                        if (masterPassword.isNotBlank() && passwordInput == masterPassword) {
                                            scope.launch { prefs.setMasterGodMode(true) }
                                            showPasswordDialog = false
                                            navController.navigate(Screen.GodMode.route)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                                ) {
                                    Text("AWAKEN GOD MODE", color = SpaceBlack, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Privacy Card
                SettingsCard(
                    icon = Icons.Default.Security,
                    title = "Privacy Policy",
                    description = "View our privacy policy",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chaoticallyorganizedai.com/privacy-policy"))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Version footer
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "LeadShield v${BuildConfig.VERSION_NAME}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Powered by Chaotically Organized AI",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeonCyan.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        cornerRadius = 24.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(NeonCyan.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}
