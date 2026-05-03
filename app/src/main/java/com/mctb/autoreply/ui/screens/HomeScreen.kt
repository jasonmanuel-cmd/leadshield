package com.mctb.autoreply.ui.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mctb.autoreply.ui.theme.TextPrimary
import com.mctb.autoreply.ui.theme.TextTertiary
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mctb.autoreply.BuildConfig
import com.mctb.autoreply.R
import com.mctb.autoreply.data.AppDatabase
import com.mctb.autoreply.data.AppPreferences
import com.mctb.autoreply.data.LeadCardEntity
import com.mctb.autoreply.data.LeadScoringEngine
import com.mctb.autoreply.data.SubscriptionManager
import com.mctb.autoreply.data.SubscriptionStatus
import com.mctb.autoreply.data.SubscriptionTier
import com.mctb.autoreply.service.CallMonitorService
import com.mctb.autoreply.worker.ReviewRequestWorker
import com.mctb.autoreply.ui.components.AnimatedMeshBackground
import com.mctb.autoreply.ui.components.GlassCard
import com.mctb.autoreply.ui.navigation.Screen
import com.mctb.autoreply.ui.theme.ErrorRed
import com.mctb.autoreply.ui.theme.NeonCyan
import com.mctb.autoreply.ui.theme.NeonCyanDark
import com.mctb.autoreply.ui.theme.NeonCyanLight
import com.mctb.autoreply.ui.theme.NeonGold
import com.mctb.autoreply.ui.theme.NeonPink
import com.mctb.autoreply.ui.theme.NeonPurple
import com.mctb.autoreply.ui.theme.SpaceBlack
import com.mctb.autoreply.ui.theme.SpaceBlackCard
import com.mctb.autoreply.ui.theme.SpaceBlackLight
import com.mctb.autoreply.ui.theme.SuccessGreen
import com.mctb.autoreply.ui.theme.SurfaceBorder
import com.mctb.autoreply.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    prefs: AppPreferences,
    subscriptionManager: SubscriptionManager
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val isEnabled by prefs.isEnabled.collectAsState(initial = false)
    val message by prefs.message.collectAsState(initial = AppPreferences.DEFAULT_MESSAGE)
    val sentCount by prefs.autoTextCount.collectAsState(initial = 0)
    val pausedUntil by prefs.pausedUntil.collectAsState(initial = 0L)
    val freeTierLimit = AppPreferences.FREE_TIER_MONTHLY_LIMIT
    val tradeJobValue by prefs.tradeJobValue.collectAsState(initial = 750)
    val db = remember { AppDatabase.getDatabase(context) }
    val recentLeads by db.leadCardDao().getAllLeadsScoredDesc().collectAsState(initial = emptyList())
    // Only show active (non-complete, non-lost) leads
    val activeLeads = recentLeads.filter { it.status !in listOf("booked", "complete", "lost") }.take(5)

    val isPaused = pausedUntil > System.currentTimeMillis()

    val status by subscriptionManager.subscriptionStatus.collectAsState(initial = SubscriptionStatus(SubscriptionTier.FREE, 0))

    // Only dangerous permissions need runtime requests.
    // MANAGE_OWN_CALLS is a normal permission (auto-granted at install).
    // RECEIVE_SMS is not used by this app.
    val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.SEND_SMS,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.READ_CONTACTS
        )
    } else {
        listOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_CONTACTS
        )
    }

    val permissionsState = rememberMultiplePermissionsState(permissions = requiredPermissions)

    LaunchedEffect(Unit) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    // Start service if auto-reply is enabled
    LaunchedEffect(isEnabled) {
        if (isEnabled && permissionsState.allPermissionsGranted) {
            CallMonitorService.start(context)
        } else if (!isEnabled) {
            CallMonitorService.stop(context)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
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
                                tint = SpaceBlack,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "LeadShield",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { navController.navigate(Screen.Settings.route) },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.05f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings),
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = SpaceBlack
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedMeshBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Master Switch Card
                MasterSwitchCard(
                    isEnabled = isEnabled,
                    isServiceRunning = isEnabled && permissionsState.allPermissionsGranted,
                    onToggle = { enabled ->
                        scope.launch {
                            prefs.setEnabled(enabled)
                            if (enabled) {
                                CallMonitorService.start(context)
                            } else {
                                CallMonitorService.stop(context)
                            }
                        }
                    }
                )

                // Permissions Warning
                if (!permissionsState.allPermissionsGranted) {
                    PermissionWarningCard(
                        onRequestPermissions = {
                            permissionsState.launchMultiplePermissionRequest()
                        }
                    )
                }

                // Message Preview Card (Clickable)
                MessagePreviewCard(
                    message = message,
                    onClick = { navController.navigate(Screen.MessageEditor.route) }
                )

                Text(
                    text = "QUICK ACTIONS",
                    style = MaterialTheme.typography.labelLarge,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        icon = Icons.Default.Edit,
                        title = "Edit Message",
                        onClick = { navController.navigate(Screen.MessageEditor.route) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        icon = Icons.Default.Schedule,
                        title = "Work Hours",
                        onClick = { navController.navigate(Screen.ActiveHours.route) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        icon = Icons.Default.History,
                        title = "History",
                        onClick = { navController.navigate(Screen.History.route) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        icon = Icons.Default.Email,
                        title = "Contacts",
                        onClick = {
                            scope.launch {
                                if (subscriptionManager.canUseFeature(SubscriptionManager.Feature.CUSTOM_CONTACTS)) {
                                    navController.navigate(Screen.ContactOverrides.route)
                                } else {
                                    navController.navigate(Screen.Subscription.route)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        tint = if (status.tier.id >= SubscriptionTier.OPERATOR.id) NeonCyan else Color.Gray
                    )
                }

                QuickActionCard(
                    icon = Icons.Default.Pause,
                    title = if (isPaused) "Resume Auto-Reply" else "Pause for 1 Hour",
                    onClick = {
                        scope.launch {
                            if (isPaused) {
                                prefs.clearPause()
                                Toast.makeText(context, "Auto-reply resumed", Toast.LENGTH_SHORT).show()
                            } else {
                                prefs.pauseForOneHour()
                                Toast.makeText(context, "Paused for 1 hour", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    tint = if (isPaused) SuccessGreen else NeonCyan
                )

                // Trial / Master Status Card
                TrialStatusCard(
                    tier = status.tier,
                    onUpgrade = { navController.navigate(Screen.Subscription.route) }
                )

                // Usage Card
                UsageCard(
                    sentCount = sentCount,
                    limit = if (status.tier.isUnlimited) 999999 else status.tier.monthlyLimit,
                    onManageClick = { navController.navigate(Screen.Subscription.route) }
                )

                // Money Captured Card
                if (sentCount > 0) {
                    MoneyCapturedCard(sentCount = sentCount, jobValue = tradeJobValue)
                }

                // Active Leads Section
                if (activeLeads.isNotEmpty()) {
                    Text(
                        text = "ACTIVE LEADS",
                        style = MaterialTheme.typography.labelLarge,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                    activeLeads.forEach { lead ->
                        LeadCardRow(
                            lead = lead,
                            onMarkComplete = {
                                scope.launch {
                                    db.leadCardDao().updateStatus(
                                        phoneNumber = lead.phoneNumber,
                                        status = "booked",
                                        updatedAt = System.currentTimeMillis()
                                    )
                                    ReviewRequestWorker.schedule(
                                        context = context,
                                        phoneNumber = lead.phoneNumber,
                                        contactName = lead.contactName
                                    )
                                    Toast.makeText(context, "Lead marked complete! Review request scheduled.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun MasterSwitchCard(
    isEnabled: Boolean,
    isServiceRunning: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val glowIntensity by animateFloatAsState(
        targetValue = if (isEnabled) 1f else 0.1f,
        animationSpec = tween(1000),
        label = "glow"
    )

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 28.dp // Extra rounded for premium feel
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isEnabled) stringResource(R.string.status_active) else stringResource(R.string.status_inactive),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isEnabled) NeonCyan else TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isServiceRunning) SuccessGreen else TextTertiary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isServiceRunning) "System Live" else "System Standby",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                }
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = NeonCyan,
                    checkedTrackColor = NeonCyan.copy(alpha = 0.3f),
                    uncheckedThumbColor = TextTertiary,
                    uncheckedTrackColor = Color.White.copy(alpha = 0.1f),
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun MessagePreviewCard(
    message: String,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        cornerRadius = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.reply_message_preview_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = NeonCyan
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_message_button),
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (message.isNotBlank()) message else stringResource(R.string.no_message_set),
                style = MaterialTheme.typography.bodyMedium,
                color = if (message.isNotBlank()) MaterialTheme.colorScheme.onBackground else TextSecondary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            if (message.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${message.length}/160 characters",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (message.length <= 160) SuccessGreen else ErrorRed
                )
            }
        }
    }
}

@Composable
fun QuickActionCard(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = NeonCyan
) {
    GlassCard(
        modifier = modifier.height(110.dp), // Fixed height for alignment
        cornerRadius = 24.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun UsageCard(
    sentCount: Int,
    limit: Int,
    onManageClick: () -> Unit
) {
    // Check if this is the Master version
    val isMasterVersion = BuildConfig.FLAVOR == "master"

    val progress by animateFloatAsState(
        targetValue = if (isMasterVersion) 0f else (sentCount.toFloat() / limit).coerceIn(0f, 1f),
        label = "progress"
    )

    val isAtLimit = !isMasterVersion && sentCount >= limit

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isAtLimit) ErrorRed.copy(alpha = 0.1f) else NeonCyan.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = if (isAtLimit) ErrorRed else NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.usage_label),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Text(
                    text = if (isMasterVersion) "Elite Status" else "$sentCount / $limit",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isAtLimit) ErrorRed else NeonCyan
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Premium Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = if (isAtLimit) {
                                    listOf(ErrorRed, ErrorRed.copy(alpha = 0.6f))
                                } else {
                                    listOf(NeonCyan, NeonPurple)
                                }
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isMasterVersion) {
                    "You have unlimited access to all features."
                } else if (isAtLimit) {
                    stringResource(R.string.limit_reached_desc)
                } else {
                    "System has efficiently managed $sentCount of your $limit available response slots."
                },
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            if (isAtLimit || isFreeTier(sentCount, limit)) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onManageClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPink // Using gold/amber for CTAs
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = if (isAtLimit) "UPGRADE TO UNLIMITED" else "VIEW PREMIUM PLANS",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = SpaceBlack
                    )
                }
            }
        }
    }
}

private fun isFreeTier(sentCount: Int, limit: Int): Boolean {
    // Helper to determine if we should show the upgrade button even if not at limit
    return limit <= 5 // Assuming 5 is free tier limit
}

@Composable
fun LeadCardRow(
    lead: LeadCardEntity,
    onMarkComplete: () -> Unit
) {
    val scoreColor = when (lead.leadScore) {
        2 -> NeonCyan   // HOT
        1 -> NeonGold   // WARM
        else -> TextSecondary  // COLD
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = LeadScoringEngine.scoreLabel(lead.leadScore),
                        style = MaterialTheme.typography.labelSmall,
                        color = scoreColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = lead.contactName ?: lead.phoneNumber,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (lead.serviceNeeded != null) {
                    Text(
                        text = lead.serviceNeeded,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Button(
                onClick = onMarkComplete,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Done",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = SpaceBlack
                )
            }
        }
    }
}

@Composable
fun MoneyCapturedCard(sentCount: Int, jobValue: Int) {
    val low = (sentCount * jobValue * 0.3).toInt()
    val high = (sentCount * jobValue * 0.7).toInt()

    fun formatMoney(amount: Int): String {
        return if (amount >= 1000) {
            "$${amount / 1000},${(amount % 1000).toString().padStart(3, '0')}"
        } else {
            "$$amount"
        }
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(NeonGold.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = null,
                        tint = NeonGold,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "MONEY CAPTURED",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = NeonGold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${formatMoney(low)} – ${formatMoney(high)} estimated this month",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = NeonGold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Based on $sentCount auto-replies sent",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun PermissionWarningCard(
    onRequestPermissions: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        cornerRadius = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ErrorRed.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = ErrorRed,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.permissions_required_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = ErrorRed
                )
                Text(
                    text = stringResource(R.string.permission_denied),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Button(
                onClick = onRequestPermissions,
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.grant_permissions),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun TrialStatusCard(
    tier: SubscriptionTier,
    onUpgrade: () -> Unit
) {
    val isFree = tier == SubscriptionTier.FREE
    val isPremiumTier = tier != SubscriptionTier.FREE

    GlassCard(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        cornerRadius = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isPremiumTier) NeonCyan.copy(alpha = 0.2f) else SuccessGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPremiumTier) Icons.Default.Bolt else Icons.Default.Schedule,
                    contentDescription = null,
                    tint = if (isPremiumTier) NeonCyan else SuccessGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${tier.name} TIER",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = if (tier.isUnlimited) "Unlimited Responses" else "${tier.monthlyLimit} / month limit",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (tier != SubscriptionTier.FREE) NeonCyan else Color.White.copy(alpha = 0.7f)
                )
            }

            if (isFree) {
                Button(
                    onClick = onUpgrade,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("UPGRADE", color = SpaceBlack, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
