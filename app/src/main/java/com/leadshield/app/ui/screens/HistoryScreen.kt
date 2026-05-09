package com.leadshield.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.leadshield.app.data.CallHistoryEntry
import com.leadshield.app.data.CallHistoryManager
import com.leadshield.app.ui.components.AnimatedMeshBackground
import com.leadshield.app.ui.components.GlassCard
import com.leadshield.app.ui.theme.ErrorRed
import com.leadshield.app.ui.theme.NeonCyan
import com.leadshield.app.ui.theme.NeonPurple
import com.leadshield.app.ui.theme.SuccessGreen
import com.leadshield.app.ui.theme.TextPrimary
import com.leadshield.app.ui.theme.TextSecondary
import com.leadshield.app.ui.theme.TextTertiary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {
    val context = LocalContext.current
    val historyManager = remember { CallHistoryManager(context) }
    val scope = rememberCoroutineScope()
    val history by historyManager.history.collectAsState(initial = emptyList())
    var selectedFilter by remember { mutableStateOf("ALL") }

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
                                text = "Analytics",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.navigateUp() },
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
                    actions = {
                        if (history.isNotEmpty()) {
                            IconButton(onClick = { scope.launch { historyManager.clearHistory() } }) {
                                Icon(Icons.Default.Delete, contentDescription = "Clear", tint = TextSecondary)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Status Filter Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip("ALL", selectedFilter == "ALL") { selectedFilter = it }
                    FilterChip("HOT", selectedFilter == "HOT") { selectedFilter = it }
                    FilterChip("NEW", selectedFilter == "NEW") { selectedFilter = it }
                    FilterChip("DONE", selectedFilter == "DONE") { selectedFilter = it }
                }

                if (history.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No activity detected yet", color = TextSecondary, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                } else {
                    val filteredHistory = if (selectedFilter == "ALL") history else history.filter { it.status == selectedFilter }
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        items(filteredHistory, key = { "${it.phoneNumber}_${it.timestamp}" }) { entry ->
                            HistoryEntryCard(entry, historyManager)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChip(label: String, isSelected: Boolean, onClick: (String) -> Unit) {
    Surface(
        onClick = { onClick(label) },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) NeonCyan.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp, 
            color = if (isSelected) NeonCyan else Color.White.copy(alpha = 0.1f)
        ),
        modifier = Modifier.height(36.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) NeonCyan else TextSecondary
            )
        }
    }
}

@Composable
fun HistoryEntryCard(entry: CallHistoryEntry, historyManager: CallHistoryManager) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showRenameDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(entry.manualName ?: "") }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Indicator
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(
                            when (entry.status) {
                                "HOT" -> ErrorRed
                                "DONE" -> SuccessGreen
                                else -> NeonCyan
                            }
                        )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.getDisplayName(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.clickable { showRenameDialog = true }
                    )
                    Text(
                        text = entry.phoneNumber,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
                Text(
                    text = entry.getFormattedTime(),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "\"${entry.messageSent}\"",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Call Button
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${entry.phoneNumber}"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Call Back", style = MaterialTheme.typography.labelMedium)
                }

                // Status Cycle Button
                Button(
                    onClick = {
                        val nextStatus = when (entry.status) {
                            "NEW" -> "HOT"
                            "HOT" -> "DONE"
                            "DONE" -> "SPAM"
                            else -> "NEW"
                        }
                        scope.launch { historyManager.updateEntry(entry.phoneNumber, entry.timestamp, newStatus = nextStatus) }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (entry.status) {
                            "HOT" -> ErrorRed.copy(alpha = 0.2f)
                            "DONE" -> SuccessGreen.copy(alpha = 0.2f)
                            "SPAM" -> Color.Gray.copy(alpha = 0.2f)
                            else -> NeonCyan.copy(alpha = 0.2f)
                        },
                        contentColor = when (entry.status) {
                            "HOT" -> ErrorRed
                            "DONE" -> SuccessGreen
                            "SPAM" -> Color.Gray
                            else -> NeonCyan
                        }
                    )
                ) {
                    Text(entry.status, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }

    if (showRenameDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showRenameDialog = false }) {
            GlassCard(cornerRadius = 24.dp) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Rename Lead", fontWeight = FontWeight.Bold, color = NeonCyan, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = newName,
                        onValueChange = { newName = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.05f),
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        placeholder = { Text("Enter name", color = TextTertiary) }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showRenameDialog = false }) {
                            Text("CANCEL", color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                scope.launch { historyManager.updateEntry(entry.phoneNumber, entry.timestamp, newName = newName) }
                                showRenameDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                        ) {
                            Text("SAVE", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
