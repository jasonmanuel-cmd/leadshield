package com.leadshield.app.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.leadshield.app.data.BillingManager
import com.leadshield.app.data.SubscriptionManager
import com.leadshield.app.data.SubscriptionStatus
import com.leadshield.app.data.SubscriptionTier
import com.leadshield.app.ui.components.AnimatedMeshBackground
import com.leadshield.app.ui.components.GlassCard
import com.leadshield.app.ui.navigation.Screen
import com.leadshield.app.ui.theme.NeonCyan
import com.leadshield.app.ui.theme.NeonGold
import com.leadshield.app.ui.theme.NeonPurple
import com.leadshield.app.ui.theme.SpaceBlack
import com.leadshield.app.ui.theme.TextPrimary
import com.leadshield.app.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    navController: NavController,
    subscriptionManager: SubscriptionManager,
    billingManager: BillingManager
) {
    val status by subscriptionManager.subscriptionStatus.collectAsState(
        initial = SubscriptionStatus(SubscriptionTier.FREE, 0)
    )
    val productDetailsList by billingManager.productDetailsList.collectAsState()
    val context = LocalContext.current
    val activity = context as Activity

    val currentTier = status.tier
    val hasPreviousScreen = navController.previousBackStackEntry != null

    // Find products from billing
    val proProduct = productDetailsList.firstOrNull { it.productId == BillingManager.PRODUCT_PRO }
    val operatorProduct = productDetailsList.firstOrNull { it.productId == BillingManager.PRODUCT_OPERATOR }
    val voiceProduct = productDetailsList.firstOrNull { it.productId == BillingManager.PRODUCT_VOICE }
    val teamProduct = productDetailsList.firstOrNull { it.productId == BillingManager.PRODUCT_TEAM }

    val proPrice = proProduct
        ?.subscriptionOfferDetails?.get(0)
        ?.pricingPhases?.pricingPhaseList?.get(0)
        ?.formattedPrice ?: "$7.99"

    Scaffold(
        topBar = {
            if (hasPreviousScreen) {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .padding(8.dp)
                                .background(Color.White.copy(alpha = 0.05f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        },
        containerColor = SpaceBlack
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedMeshBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(if (hasPreviousScreen) 8.dp else 48.dp))

                // Headline
                Text(
                    text = "Never lose a job to a\nmissed call again.",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Every tier auto-replies to missed calls.\nOperator adds your AI receptionist.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                // ── FREE card ─────────────────────────────────────────
                PlanCard(
                    name = "Free",
                    badge = if (currentTier == SubscriptionTier.FREE) "CURRENT PLAN" else null,
                    badgeColor = TextSecondary,
                    price = "$0",
                    priceSuffix = "/mo",
                    tagline = "Try it out, no commitment.",
                    bullets = listOf(
                        "10 auto-replies per month",
                        "One custom message",
                        "Basic activity log",
                        "Includes ads"
                    ),
                    bulletType = BulletType.PLAIN,
                    highlightColor = TextSecondary,
                    borderBrush = null,
                    buttonText = null,  // no action for free
                    onButtonClick = {}
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ── PRO card ──────────────────────────────────────────
                PlanCard(
                    name = "Pro",
                    badge = when (currentTier) {
                        SubscriptionTier.PRO -> "CURRENT PLAN"
                        else -> "MOST POPULAR"
                    },
                    badgeColor = NeonCyan,
                    price = proPrice,
                    priceSuffix = "/mo",
                    tagline = "For trades that can't miss hot leads.",
                    bullets = listOf(
                        "Unlimited auto-replies",
                        "No ads",
                        "Reply delay so texts feel human",
                        "Priority support"
                    ),
                    bulletType = BulletType.CHECK,
                    highlightColor = NeonCyan,
                    borderBrush = Brush.linearGradient(colors = listOf(NeonCyan, NeonPurple)),
                    buttonText = if (currentTier == SubscriptionTier.PRO) null else "Upgrade to Pro",
                    onButtonClick = {
                        proProduct?.let { billingManager.launchBillingFlow(activity, it) }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ── OPERATOR card ─────────────────────────────────────
                PlanCard(
                    name = "Operator",
                    badge = when (currentTier) {
                        SubscriptionTier.OPERATOR, SubscriptionTier.MASTER,
                        SubscriptionTier.VOICE, SubscriptionTier.TEAM -> "CURRENT PLAN"
                        else -> "AI POWERED"
                    },
                    badgeColor = NeonGold,
                    price = "$49",
                    priceSuffix = "/mo",
                    tagline = "Your AI receptionist — captures leads while you work.",
                    bullets = listOf(
                        "Unlimited auto-replies",
                        "AI continues the text conversation for you",
                        "Captures caller name, need & contact info",
                        "Smart VIP & lead routing · No ads"
                    ),
                    bulletType = BulletType.STAR,
                    highlightColor = NeonGold,
                    borderBrush = Brush.linearGradient(colors = listOf(NeonGold, NeonCyan.copy(alpha = 0.7f))),
                    buttonText = when (currentTier) {
                        SubscriptionTier.OPERATOR, SubscriptionTier.MASTER,
                        SubscriptionTier.VOICE, SubscriptionTier.TEAM -> null
                        else -> "Get Operator — $49/mo"
                    },
                    onButtonClick = {
                        operatorProduct?.let { billingManager.launchBillingFlow(activity, it) }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ── VOICE card ────────────────────────────────────────
                PlanCard(
                    name = "Voice",
                    badge = when (currentTier) {
                        SubscriptionTier.VOICE -> "CURRENT PLAN"
                        else -> "COMING SOON"
                    },
                    badgeColor = NeonPurple,
                    price = "$99",
                    priceSuffix = "/mo",
                    tagline = "Your AI actually answers the phone.",
                    bullets = listOf(
                        "AI answers missed calls with your voice",
                        "Full conversation — captures job details",
                        "Everything in Operator included",
                        "Available 24/7, sounds like your business"
                    ),
                    bulletType = BulletType.STAR,
                    highlightColor = NeonPurple,
                    borderBrush = Brush.linearGradient(listOf(NeonPurple, NeonGold.copy(alpha = 0.7f))),
                    buttonText = if (currentTier == SubscriptionTier.VOICE) null else "Join Waitlist — Voice",
                    onButtonClick = {
                        if (voiceProduct != null) {
                            billingManager.launchBillingFlow(activity, voiceProduct)
                        } else {
                            Toast.makeText(context, "Coming soon — join waitlist", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ── TEAM card ─────────────────────────────────────────
                PlanCard(
                    name = "Team",
                    badge = when (currentTier) {
                        SubscriptionTier.TEAM -> "CURRENT PLAN"
                        else -> "COMING SOON"
                    },
                    badgeColor = Color(0xFF00E5FF),
                    price = "$129",
                    priceSuffix = "/mo",
                    tagline = "For crews. Multiple phones, one dashboard.",
                    bullets = listOf(
                        "Up to 3 phones on one account",
                        "Route calls to the right crew member",
                        "Shared lead pipeline & CRM",
                        "Everything in Voice included"
                    ),
                    bulletType = BulletType.STAR,
                    highlightColor = Color(0xFF00E5FF),
                    borderBrush = Brush.linearGradient(listOf(Color(0xFF00E5FF), NeonPurple)),
                    buttonText = if (currentTier == SubscriptionTier.TEAM) null else "Join Waitlist — Team",
                    onButtonClick = {
                        Toast.makeText(context, "Coming soon — join waitlist", Toast.LENGTH_SHORT).show()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "One saved lead pays for Operator for months.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeonGold,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Continue with Free link
                if (currentTier == SubscriptionTier.FREE) {
                    TextButton(
                        onClick = {
                            if (hasPreviousScreen) navController.popBackStack()
                            else navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Subscription.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Continue with Free plan",
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = "Cancel anytime. Billed monthly through Google Play.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

enum class BulletType { PLAIN, CHECK, STAR }

@Composable
private fun PlanCard(
    name: String,
    badge: String?,
    badgeColor: Color,
    price: String,
    priceSuffix: String,
    tagline: String,
    bullets: List<String>,
    bulletType: BulletType,
    highlightColor: Color,
    borderBrush: Brush?,
    buttonText: String?,
    onButtonClick: () -> Unit
) {
    val cardModifier = if (borderBrush != null) {
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        highlightColor.copy(alpha = 0.08f),
                        Color.White.copy(alpha = 0.03f)
                    )
                )
            )
            .border(1.5.dp, borderBrush, RoundedCornerShape(20.dp))
    } else {
        Modifier.fillMaxWidth()
    }

    if (borderBrush != null) {
        Box(modifier = cardModifier) {
            CardContent(name, badge, badgeColor, price, priceSuffix, tagline, bullets, bulletType, highlightColor, buttonText, onButtonClick)
        }
    } else {
        GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
            CardContent(name, badge, badgeColor, price, priceSuffix, tagline, bullets, bulletType, highlightColor, buttonText, onButtonClick)
        }
    }
}

@Composable
private fun CardContent(
    name: String,
    badge: String?,
    badgeColor: Color,
    price: String,
    priceSuffix: String,
    tagline: String,
    bullets: List<String>,
    bulletType: BulletType,
    highlightColor: Color,
    buttonText: String?,
    onButtonClick: () -> Unit
) {
    Column(modifier = Modifier.padding(20.dp)) {
        // Name + badge row
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = highlightColor
            )
            if (badge != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Price
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = price,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(
                text = priceSuffix,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
            )
        }

        Text(
            text = tagline,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Bullets
        bullets.forEach { bullet ->
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                when (bulletType) {
                    BulletType.PLAIN -> Text("•", color = TextSecondary, fontSize = 12.sp,
                        modifier = Modifier.padding(top = 1.dp, end = 8.dp))
                    BulletType.CHECK -> Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp).padding(top = 1.dp)
                    )
                    BulletType.STAR -> Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = NeonGold,
                        modifier = Modifier.size(14.dp).padding(top = 1.dp)
                    )
                }
                if (bulletType != BulletType.PLAIN) Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = bullet,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (bulletType == BulletType.PLAIN) TextSecondary else TextPrimary
                )
            }
        }

        // Action button
        if (buttonText != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onButtonClick,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = highlightColor),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = buttonText,
                    color = SpaceBlack,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                )
            }
        }
    }
}
