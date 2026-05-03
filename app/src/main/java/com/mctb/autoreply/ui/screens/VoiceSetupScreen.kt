package com.mctb.autoreply.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mctb.autoreply.data.AppPreferences
import com.mctb.autoreply.ui.components.AnimatedMeshBackground
import com.mctb.autoreply.ui.components.GlassCard
import com.mctb.autoreply.ui.theme.NeonCyan
import com.mctb.autoreply.ui.theme.NeonPurple
import com.mctb.autoreply.ui.theme.SpaceBlack
import com.mctb.autoreply.ui.theme.TextPrimary
import com.mctb.autoreply.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceSetupScreen(
    navController: NavController,
    prefs: AppPreferences
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val savedGreeting by prefs.voiceGreeting.collectAsState(initial = "")
    var greetingScript by remember(savedGreeting) { mutableStateOf(savedGreeting) }
    var businessPhone by remember { mutableStateOf("") }
    var selectedVoiceStyle by remember { mutableStateOf("Friendly") }

    val voiceStyles = listOf("Professional", "Friendly", "Direct")

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NeonPurple,
        unfocusedBorderColor = TextSecondary.copy(alpha = 0.4f),
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        focusedLabelColor = NeonPurple,
        unfocusedLabelColor = TextSecondary,
        cursorColor = NeonPurple,
        focusedContainerColor = Color.White.copy(alpha = 0.05f),
        unfocusedContainerColor = Color.White.copy(alpha = 0.03f)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AI Voice Setup",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(8.dp)
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = SpaceBlack
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedMeshBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // COMING SOON banner
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 16.dp,
                    glowColor = NeonPurple.copy(alpha = 0.2f),
                    borderColor = NeonPurple
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "COMING SOON",
                            style = MaterialTheme.typography.labelMedium,
                            color = NeonPurple,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "AI Voice Answering is launching soon. Set up your profile now and you'll be first in line.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Greeting Script
                Text(
                    text = "GREETING SCRIPT",
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonPurple,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = greetingScript,
                    onValueChange = { greetingScript = it },
                    label = { Text("Greeting Script") },
                    placeholder = {
                        Text(
                            "Hi, thanks for calling [Business Name]! I'm [Owner]'s assistant. He's on a job right now — can I get your name and what you need help with today?",
                            color = TextSecondary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Business Phone Number
                Text(
                    text = "TWILIO FORWARDING NUMBER",
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonPurple,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = businessPhone,
                    onValueChange = { businessPhone = it },
                    label = { Text("Business Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Voice Style chips
                Text(
                    text = "VOICE STYLE",
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonPurple,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    voiceStyles.forEach { style ->
                        FilterChip(
                            selected = selectedVoiceStyle == style,
                            onClick = { selectedVoiceStyle = style },
                            label = { Text(style) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonPurple.copy(alpha = 0.2f),
                                selectedLabelColor = NeonPurple,
                                containerColor = Color.White.copy(alpha = 0.05f),
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedVoiceStyle == style,
                                selectedBorderColor = NeonPurple,
                                borderColor = TextSecondary.copy(alpha = 0.3f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // How it works
                Text(
                    text = "HOW IT WORKS",
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16.dp) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        listOf(
                            "1. Customer calls your number",
                            "2. AI picks up after 2 rings",
                            "3. Full conversation — captures job details",
                            "4. You get a summary text immediately"
                        ).forEach { step ->
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Save & Join Waitlist button
                Button(
                    onClick = {
                        scope.launch {
                            prefs.setVoiceGreeting(greetingScript)
                            snackbarHostState.showSnackbar(
                                "You're on the Voice waitlist! We'll notify you when it launches."
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Save & Join Voice Waitlist",
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
