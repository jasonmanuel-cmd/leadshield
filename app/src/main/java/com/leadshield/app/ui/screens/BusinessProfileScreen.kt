package com.leadshield.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.leadshield.app.data.AppPreferences
import com.leadshield.app.ui.components.AnimatedMeshBackground
import com.leadshield.app.ui.components.GlassCard
import com.leadshield.app.ui.theme.NeonCyan
import com.leadshield.app.ui.theme.NeonGold
import com.leadshield.app.ui.theme.NeonPurple
import com.leadshield.app.ui.theme.SpaceBlack
import com.leadshield.app.ui.theme.TextPrimary
import com.leadshield.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessProfileScreen(
    navController: NavController,
    prefs: AppPreferences
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Pre-populate from prefs
    var ownerName by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var tradeType by remember { mutableStateOf("") }
    var serviceArea by remember { mutableStateOf("") }
    var callbackWindow by remember { mutableStateOf("within 1 hour") }
    var pricingInfo by remember { mutableStateOf("") }
    var aiConversationEnabled by remember { mutableStateOf(true) }
    var googleReviewUrl by remember { mutableStateOf("") }

    // Load saved values once
    val savedProfile by prefs.businessName.collectAsState(initial = "")
    val savedOwnerName by prefs.ownerName.collectAsState(initial = "")
    val savedTradeType by prefs.tradeType.collectAsState(initial = "")
    val savedServiceArea by prefs.serviceArea.collectAsState(initial = "")
    val savedCallbackWindow by prefs.callbackWindow.collectAsState(initial = "within 1 hour")
    val savedPricingInfo by prefs.pricingInfo.collectAsState(initial = "")
    val savedAiEnabled by prefs.aiConversationEnabled.collectAsState(initial = true)
    val savedGoogleReviewUrl by prefs.googleReviewUrl.collectAsState(initial = "")

    var initialized by remember { mutableStateOf(false) }
    LaunchedEffect(
        savedProfile, savedOwnerName, savedTradeType,
        savedServiceArea, savedCallbackWindow, savedPricingInfo, savedAiEnabled, savedGoogleReviewUrl
    ) {
        if (!initialized) {
            businessName = savedProfile
            ownerName = savedOwnerName
            tradeType = savedTradeType
            serviceArea = savedServiceArea
            callbackWindow = savedCallbackWindow
            pricingInfo = savedPricingInfo
            aiConversationEnabled = savedAiEnabled
            googleReviewUrl = savedGoogleReviewUrl
            initialized = true
        }
    }

    var showSavedSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(showSavedSnackbar) {
        if (showSavedSnackbar) {
            snackbarHostState.showSnackbar("Profile saved")
            showSavedSnackbar = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedMeshBackground()

        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
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
                                    imageVector = Icons.Default.Business,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Business Profile",
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
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = SpaceBlack.copy(alpha = 0.85f)
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Info card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    glowColor = NeonCyan.copy(alpha = 0.15f),
                    borderColor = NeonCyan
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Operator AI Receptionist",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                            Text(
                                text = "LeadShield Operator uses this info to answer questions from callers while you're on the job. " +
                                        "The more detail you provide, the better your AI receptionist performs.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // AI Conversation toggle
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "AI Conversation",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "When callers text back, our AI continues the conversation and captures the lead.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Switch(
                            checked = aiConversationEnabled,
                            onCheckedChange = { enabled ->
                                aiConversationEnabled = enabled
                                scope.launch { prefs.setAiConversationEnabled(enabled) }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SpaceBlack,
                                checkedTrackColor = NeonCyan,
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
                            )
                        )
                    }
                }

                // Form fields section header
                Text(
                    text = "Your Business Details",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = NeonGold,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )

                // Owner Name
                ProfileTextField(
                    value = ownerName,
                    onValueChange = { ownerName = it },
                    label = "Your Name",
                    placeholder = "e.g. Mike Johnson"
                )

                // Business Name
                ProfileTextField(
                    value = businessName,
                    onValueChange = { businessName = it },
                    label = "Business Name",
                    placeholder = "e.g. Johnson Plumbing LLC"
                )

                // Trade / Service Type
                ProfileTextField(
                    value = tradeType,
                    onValueChange = { tradeType = it },
                    label = "Trade / Service Type",
                    placeholder = "e.g. Plumber, Electrician, HVAC, General Contractor"
                )

                // Service Area
                ProfileTextField(
                    value = serviceArea,
                    onValueChange = { serviceArea = it },
                    label = "Service Area",
                    placeholder = "e.g. Austin, TX and surrounding areas"
                )

                // Callback Timeframe
                ProfileTextField(
                    value = callbackWindow,
                    onValueChange = { callbackWindow = it },
                    label = "Callback Timeframe",
                    placeholder = "e.g. within 1 hour, same day, within 2 hours"
                )

                // Pricing Info (optional)
                OutlinedTextField(
                    value = pricingInfo,
                    onValueChange = { pricingInfo = it },
                    label = {
                        Text(
                            text = "Pricing Info (optional)",
                            color = TextSecondary
                        )
                    },
                    placeholder = {
                        Text(
                            text = "e.g. Water heater replacement: \$350-550\nDrain cleaning: \$150",
                            color = TextSecondary.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = NeonCyan,
                        focusedContainerColor = Color.White.copy(alpha = 0.04f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.04f)
                    )
                )

                // Google Review Link
                ProfileTextField(
                    value = googleReviewUrl,
                    onValueChange = { googleReviewUrl = it },
                    label = "Google Review Link",
                    placeholder = "https://g.page/r/your-business/review"
                )
                Text(
                    text = "We'll text this to customers 24hrs after job complete",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                )

                // Save button
                Button(
                    onClick = {
                        scope.launch {
                            prefs.saveBusinessProfile(
                                businessName = businessName.trim(),
                                ownerName = ownerName.trim(),
                                tradeType = tradeType.trim(),
                                serviceArea = serviceArea.trim(),
                                callbackWindow = callbackWindow.trim(),
                                pricingInfo = pricingInfo.trim()
                            )
                            prefs.setGoogleReviewUrl(googleReviewUrl.trim())
                            showSavedSnackbar = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = SpaceBlack
                    )
                ) {
                    Text(
                        text = "Save Profile",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                color = TextSecondary
            )
        },
        placeholder = {
            Text(
                text = placeholder,
                color = TextSecondary.copy(alpha = 0.5f),
                style = MaterialTheme.typography.bodySmall
            )
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = NeonCyan,
            focusedContainerColor = Color.White.copy(alpha = 0.04f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.04f)
        )
    )
}
