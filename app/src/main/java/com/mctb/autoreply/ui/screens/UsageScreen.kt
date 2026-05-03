package com.mctb.autoreply.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mctb.autoreply.R
import com.mctb.autoreply.data.AppDatabase
import com.mctb.autoreply.data.AppPreferences
import com.mctb.autoreply.data.SubscriptionTier
import com.mctb.autoreply.ui.components.AnimatedMeshBackground
import com.mctb.autoreply.ui.components.GlassCard
import com.mctb.autoreply.ui.navigation.Screen
import com.mctb.autoreply.ui.theme.NeonCyan
import com.mctb.autoreply.ui.theme.NeonGold
import com.mctb.autoreply.ui.theme.NeonPurple
import com.mctb.autoreply.ui.theme.SpaceBlack
import com.mctb.autoreply.ui.theme.TextPrimary
import com.mctb.autoreply.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsageScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val prefs = remember { AppPreferences(context) }

    val sentCount by prefs.autoTextCount.collectAsState(initial = 0)
    val successCount by db.analyticsDao().getSuccessCount().collectAsState(initial = 0)
    val totalAttempts by db.analyticsDao().getTotalCount().collectAsState(initial = 0)
    val tier by prefs.subscriptionTier.collectAsState(initial = SubscriptionTier.FREE)
    val tradeJobValue by prefs.tradeJobValue.collectAsState(initial = 750)

    val isOnPro = tier != SubscriptionTier.FREE
    val displayLimit = 10 // only meaningful for FREE tier
    val progress = if (isOnPro) 1f else (sentCount.toFloat() / displayLimit).coerceIn(0f, 1f)
    val limitHit = !isOnPro && sentCount >= displayLimit

    val efficiency = if (totalAttempts > 0) (successCount.toFloat() / totalAttempts * 100).toInt() else 100

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    brush = Brush.linearGradient(colors = listOf(NeonCyan, NeonPurple)),
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
                            text = "Usage & Stats",
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
        containerColor = SpaceBlack
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedMeshBackground()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Cap banner — only shown when limit is hit
                if (limitHit) {
                    item {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                            cornerRadius = 20.dp
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "You've hit your 10 free auto-replies this month.",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "The next missed call won't get a text. Upgrade to Pro so every caller gets a response.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { navController.navigate(Screen.Subscription.route) },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Upgrade to Pro", color = SpaceBlack, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }

                // Weekly usage card
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "This week",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = when (tier) {
                                        SubscriptionTier.OPERATOR, SubscriptionTier.MASTER ->
                                            "Unlimited — Operator plan active"
                                        SubscriptionTier.PRO ->
                                            "Unlimited — Pro plan active ($sentCount sent)"
                                        else ->
                                            "Free: $sentCount of 10 replies used this month"
                                    },
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (isOnPro) NeonCyan else TextPrimary,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                                color = if (limitHit) MaterialTheme.colorScheme.error else NeonCyan,
                                trackColor = Color.White.copy(alpha = 0.05f)
                            )

                            if (!isOnPro) {
                                Spacer(modifier = Modifier.height(12.dp))
                                TextButton(
                                    onClick = { navController.navigate(Screen.Subscription.route) },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text(
                                        text = if (limitHit) "Upgrade to Pro so you never hit the limit →"
                                               else "Upgrade to Pro for unlimited replies →",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = NeonGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Stats grid
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = "Success Rate",
                            value = "$efficiency%",
                            icon = Icons.Default.Analytics,
                            color = NeonPurple
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = "Total Sent",
                            value = totalAttempts.toString(),
                            icon = Icons.Default.History,
                            color = NeonGold
                        )
                    }
                }

                // Money Captured card
                if (sentCount > 0) {
                    item {
                        UsageScreenMoneyCapturedCard(sentCount = sentCount, jobValue = tradeJobValue)
                    }
                }

                // Pro pitch card — only shown on Free
                if (!isOnPro) {
                    item {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = NeonCyan.copy(alpha = 0.2f),
                            cornerRadius = 20.dp
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "For trades and small businesses that can't miss hot leads.",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Pro removes the monthly cap so every missed call gets a reply — not just the first 10.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { navController.navigate(Screen.Subscription.route) },
                                    modifier = Modifier.fillMaxWidth().height(52.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                                ) {
                                    Text("Upgrade to Pro", color = SpaceBlack, fontWeight = FontWeight.Black)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "One saved job pays for Pro for months.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonGold,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    GlassCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = TextPrimary)
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        }
    }
}

@Composable
fun UsageScreenMoneyCapturedCard(sentCount: Int, jobValue: Int) {
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
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
