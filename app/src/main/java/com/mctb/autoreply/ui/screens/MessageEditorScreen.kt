package com.mctb.autoreply.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mctb.autoreply.BuildConfig
import com.mctb.autoreply.R
import com.mctb.autoreply.data.AppPreferences
import com.mctb.autoreply.data.BillingManager
import com.mctb.autoreply.data.MessageTemplates
import com.mctb.autoreply.data.SubscriptionManager
import com.mctb.autoreply.data.SubscriptionStatus
import com.mctb.autoreply.data.SubscriptionTier
import com.mctb.autoreply.ui.components.AnimatedMeshBackground
import com.mctb.autoreply.ui.components.GlassCard
import com.mctb.autoreply.ui.theme.NeonCyan
import com.mctb.autoreply.ui.theme.SpaceBlack
import kotlinx.coroutines.launch

const val MAX_MESSAGE_LENGTH = 160

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageEditorScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = remember { AppPreferences(context) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val currentMessage by prefs.message.collectAsState(initial = "")
    var message by remember(currentMessage) { mutableStateOf(currentMessage) }
    var showTemplateDialog by remember { mutableStateOf(false) }

    val currentAfterHoursMessage by prefs.afterHoursMessage.collectAsState(initial = "")
    var afterHoursMessage by remember(currentAfterHoursMessage) { mutableStateOf(currentAfterHoursMessage) }
    
    val billingManager = remember { BillingManager(context) }
    val subscriptionManager = remember { SubscriptionManager(context, prefs, billingManager) }
    val status by subscriptionManager.subscriptionStatus.collectAsState(initial = SubscriptionStatus(SubscriptionTier.FREE, 0))
    val templatesAvailable = status.tier.id >= SubscriptionTier.PRO.id

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_message_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().background(SpaceBlack)) {
            AnimatedMeshBackground()
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    if (templatesAvailable) {
                        OutlinedButton(
                            onClick = { showTemplateDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = androidx.compose.ui.graphics.SolidColor(NeonCyan.copy(alpha = 0.5f)))
                        ) {
                            Text("Choose Industry Template", color = NeonCyan)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    OutlinedTextField(
                        value = message,
                        onValueChange = { if (it.length <= MAX_MESSAGE_LENGTH) message = it },
                        label = { Text(stringResource(R.string.message_label), color = NeonCyan) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 5,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedLabelColor = NeonCyan,
                            cursorColor = NeonCyan
                        ),
                        supportingText = {
                            Text(
                                text = "${message.length} / $MAX_MESSAGE_LENGTH",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "AFTER-HOURS MESSAGE",
                        style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
                        color = NeonCyan,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Different message for calls outside your active hours",
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Leave blank to use the same message all day",
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = afterHoursMessage,
                        onValueChange = { if (it.length <= MAX_MESSAGE_LENGTH) afterHoursMessage = it },
                        label = { Text("After-Hours Message (optional)", color = NeonCyan.copy(alpha = 0.7f)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan.copy(alpha = 0.7f),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedLabelColor = NeonCyan.copy(alpha = 0.7f),
                            cursorColor = NeonCyan
                        ),
                        supportingText = {
                            Text(
                                text = "${afterHoursMessage.length} / $MAX_MESSAGE_LENGTH",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End,
                                color = Color.White.copy(alpha = 0.4f)
                            )
                        }
                    )
                }
                
                Button(
                    onClick = {
                        scope.launch {
                            prefs.setMessage(message)
                            prefs.setAfterHoursMessage(afterHoursMessage)
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text(stringResource(R.string.save), fontWeight = FontWeight.Bold, color = SpaceBlack)
                }
            }
        }
    }

    if (showTemplateDialog) {
        TemplateSelectionDialog(
            onDismiss = { showTemplateDialog = false },
            onTemplateSelected = { template ->
                message = template.message
                showTemplateDialog = false
            }
        )
    }
}

@Composable
fun TemplateSelectionDialog(
    onDismiss: () -> Unit,
    onTemplateSelected: (com.mctb.autoreply.data.MessageTemplate) -> Unit
) {
    var selectedIndustry by remember { mutableStateOf<String?>(null) }
    val groupedTemplates = remember { MessageTemplates.getTemplatesByIndustry() }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White.copy(alpha = 0.7f))
            }
        },
        dismissButton = {
            if (selectedIndustry != null) {
                TextButton(onClick = { selectedIndustry = null }) {
                    Text("Back", color = NeonCyan)
                }
            }
        },
        title = { Text(if (selectedIndustry == null) "Choose Industry" else "$selectedIndustry Templates", color = NeonCyan) },
        containerColor = SpaceBlack,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(24.dp).fillMaxHeight(0.8f),
        text = {
            LazyColumn {
                if (selectedIndustry == null) {
                    items(groupedTemplates.keys.toList()) { industry ->
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedIndustry = industry },
                            cornerRadius = 16.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = industry, style = MaterialTheme.typography.titleMedium, color = Color.White)
                                Icon(Icons.Filled.ArrowForward, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                } else {
                    val templates = groupedTemplates[selectedIndustry] ?: emptyList()
                    items(templates) { template ->
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onTemplateSelected(template) },
                            cornerRadius = 16.dp
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = template.name, style = MaterialTheme.typography.titleMedium, color = Color.White)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = template.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}
