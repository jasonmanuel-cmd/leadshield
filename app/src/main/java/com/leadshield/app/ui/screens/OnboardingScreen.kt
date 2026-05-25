package com.leadshield.app.ui.screens

import android.Manifest
import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.leadshield.app.data.AppPreferences
import com.leadshield.app.ui.components.AnimatedMeshBackground
import com.leadshield.app.ui.components.GlassCard
import com.leadshield.app.ui.navigation.Screen
import com.leadshield.app.ui.theme.NeonCyan
import com.leadshield.app.ui.theme.NeonPurple
import com.leadshield.app.ui.theme.SpaceBlack
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OnboardingScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = remember { AppPreferences(context) }
    val scope = rememberCoroutineScope()
    var currentPage by remember { mutableStateOf(0) }
    var permissionPromptShown by remember { mutableStateOf(false) }

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

    val pages = listOf(
        OnboardingPageData(
            id = "intro",
            title = "LeadShield Activation",
            description = "Welcome to the next generation of communication management. Your system is ready for initialization.",
            icon = Icons.Default.Shield,
            color = NeonCyan,
            badge = "PROTOCOL 01"
        ),
        OnboardingPageData(
            id = "sms",
            title = "Secure SMS Link",
            description = "LeadShield requires high-level SMS clearance to deliver your encrypted response protocols to incoming inquiries.",
            icon = Icons.Default.Lock,
            color = NeonPurple,
            badge = "PROTOCOL 02"
        ),
        OnboardingPageData(
            id = "privacy",
            title = "Privacy Firewall",
            description = "Full control remains in your hands. Encrypted logs, zero data mining, and instant override capabilities.",
            icon = Icons.Default.Security,
            color = NeonCyan,
            badge = "PROTOCOL 03"
        )
    )

    val isLastPage = currentPage == pages.lastIndex

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedMeshBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedContentWrapper(targetState = currentPage) { targetPage ->
                val data = pages[targetPage]
                OnboardingCard(data)
            }

            Spacer(modifier = Modifier.height(48.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pages.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .size(if (currentPage == index) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (currentPage == index) NeonCyan else Color.White.copy(alpha = 0.3f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            val buttonLabel = when {
                !isLastPage -> "INITIALIZE NEXT"
                !permissionPromptShown -> "GRANT CLEARANCE"
                permissionsState.allPermissionsGranted -> "ACCESS TERMINAL"
                else -> "START ENGINE"
            }

            Button(
                onClick = {
                    when {
                        !isLastPage -> {
                            currentPage++
                        }
                        !permissionPromptShown -> {
                            permissionPromptShown = true
                            permissionsState.launchMultiplePermissionRequest()
                        }
                        else -> {
                            scope.launch {
                                prefs.setOnboarded(true)
                                // Navigate to Home first (clear onboarding), then show paywall on top
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                                }
                                navController.navigate(Screen.Subscription.route)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = buttonLabel,
                    color = SpaceBlack,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                if (!isLastPage) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = SpaceBlack, modifier = Modifier.size(20.dp))
                } else if (permissionsState.allPermissionsGranted && permissionPromptShown) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SpaceBlack, modifier = Modifier.size(20.dp))
                }
            }

            if (isLastPage) {
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    onClick = {
                        scope.launch {
                            prefs.setOnboarded(true)
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                            navController.navigate(Screen.Subscription.route)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "SKIP CLEARANCE FOR NOW",
                        color = Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }
            }

            if (isLastPage && permissionPromptShown) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (permissionsState.allPermissionsGranted) {
                        "CLEARANCE VERIFIED. SYSTEM SECURE."
                    } else {
                        "CLEARANCE PARTIAL. SOME FEATURES OFFLINE."
                    },
                    color = (if (permissionsState.allPermissionsGranted) NeonCyan else Color.Red).copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun OnboardingCard(data: OnboardingPageData) {
    val infiniteTransition = rememberInfiniteTransition(label = "icon_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 32.dp
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(data.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = data.icon,
                    contentDescription = null,
                    tint = data.color,
                    modifier = Modifier.size(48.dp)
                )
                
                // Outer ring
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .padding(2.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = data.badge,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                color = Color.White.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = data.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = data.color,
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = data.description,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AnimatedContentWrapper(
    targetState: Int,
    content: @Composable (Int) -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            (slideInHorizontally(initialOffsetX = { it / 4 }) + fadeIn()) togetherWith
                (slideOutHorizontally(targetOffsetX = { -it / 4 }) + fadeOut())
        },
        label = "onboarding_page"
    ) {
        content(it)
    }
}

data class OnboardingPageData(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val badge: String
)
