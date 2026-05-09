package com.leadshield.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.leadshield.app.data.AppPreferences
import com.leadshield.app.ui.components.AnimatedMeshBackground
import com.leadshield.app.ui.components.GlassCard
import com.leadshield.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageTemplatesScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = remember { AppPreferences(context) }
    val scope = rememberCoroutineScope()

    val savedVip by prefs.vipMessage.collectAsState(initial = AppPreferences.DEFAULT_VIP_MESSAGE)
    val savedContact by prefs.contactMessage.collectAsState(initial = AppPreferences.DEFAULT_CONTACT_MESSAGE)
    val savedLead by prefs.leadMessage.collectAsState(initial = AppPreferences.DEFAULT_MESSAGE)

    var vipDraft by remember(savedVip) { mutableStateOf(savedVip) }
    var contactDraft by remember(savedContact) { mutableStateOf(savedContact) }
    var leadDraft by remember(savedLead) { mutableStateOf(savedLead) }

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
                                        brush = Brush.linearGradient(colors = listOf(NeonCyan, NeonPurple)),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Message, null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Message Templates", fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Info card
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(NeonCyan.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Info, null, tint = NeonCyan, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Use [name] to insert the caller's name automatically",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // VIP / Family template
                TemplateCard(
                    title = "Family / VIP Message",
                    subtitle = "Sent to contacts in your VIP list",
                    accentColor = NeonGold,
                    value = vipDraft,
                    onValueChange = { vipDraft = it },
                    onSave = { scope.launch { prefs.setVipMessage(vipDraft) } }
                )

                // Known contact template
                TemplateCard(
                    title = "Known Contact Message",
                    subtitle = "Sent to people already in your phone contacts",
                    accentColor = NeonCyan,
                    value = contactDraft,
                    onValueChange = { contactDraft = it },
                    onSave = { scope.launch { prefs.setContactMessage(contactDraft) } }
                )

                // New lead template
                TemplateCard(
                    title = "New Lead Message",
                    subtitle = "Sent to unknown callers — potential new customers",
                    accentColor = NeonPurple,
                    value = leadDraft,
                    onValueChange = { leadDraft = it },
                    onSave = { scope.launch { prefs.setLeadMessage(leadDraft) } }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun TemplateCard(
    title: String,
    subtitle: String,
    accentColor: Color,
    value: String,
    onValueChange: (String) -> Unit,
    onSave: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, color = accentColor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                placeholder = { Text("Your message… use [name] for the caller's name", color = TextSecondary.copy(alpha = 0.5f)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                    cursorColor = accentColor
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${value.length}/160",
                    color = if (value.length <= 160) TextSecondary else NeonPink,
                    style = MaterialTheme.typography.labelSmall
                )
                Button(
                    onClick = onSave,
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text("Save", color = SpaceBlack, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
