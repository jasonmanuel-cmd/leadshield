package com.leadshield.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.leadshield.app.data.AppDatabase
import com.leadshield.app.data.VipContactEntity
import com.leadshield.app.ui.components.AnimatedMeshBackground
import com.leadshield.app.ui.components.GlassCard
import com.leadshield.app.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private data class PhoneContact(val name: String, val number: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VipContactsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { AppDatabase.getDatabase(context) }
    val vipContacts by db.vipContactDao().getAll().collectAsState(initial = emptyList())

    var showAddDialog by remember { mutableStateOf(false) }
    var phoneContacts by remember { mutableStateOf<List<PhoneContact>>(emptyList()) }

    // Load phone contacts
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                val list = mutableListOf<PhoneContact>()
                val cursor = context.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    ),
                    null, null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
                )
                cursor?.use {
                    val nameIdx = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val numIdx = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    while (it.moveToNext()) {
                        list.add(PhoneContact(it.getString(nameIdx), it.getString(numIdx)))
                    }
                }
                phoneContacts = list
            }
        }
    }

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
                                        brush = Brush.linearGradient(colors = listOf(NeonGold, NeonCyan)),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Star, null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("VIP Contacts", fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextPrimary)
                        }
                    },
                    actions = {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, "Add VIP", tint = NeonGold)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = NeonGold,
                    contentColor = SpaceBlack
                ) {
                    Icon(Icons.Default.Add, "Add VIP Contact")
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            if (vipContacts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Star, null, tint = NeonGold.copy(alpha = 0.4f), modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No VIP contacts yet", color = TextSecondary, style = MaterialTheme.typography.titleMedium)
                        Text("Tap + to add family, clients, or key contacts", color = TextSecondary.copy(alpha = 0.6f), style = MaterialTheme.typography.bodySmall)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(vipContacts, key = { it.phoneNumber }) { contact ->
                        VipContactRow(contact = contact, onDelete = {
                            scope.launch { db.vipContactDao().deleteByPhone(contact.phoneNumber) }
                        })
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddVipContactDialog(
            phoneContacts = phoneContacts,
            onDismiss = { showAddDialog = false },
            onAdd = { entity ->
                scope.launch {
                    db.vipContactDao().upsert(entity)
                    showAddDialog = false
                }
            }
        )
    }
}

@Composable
private fun VipContactRow(contact: VipContactEntity, onDelete: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth().animateContentSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape)
                        .background(NeonGold.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contact.contactName.firstOrNull()?.toString() ?: "?",
                        color = NeonGold,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(contact.contactName, color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text(
                        text = "\"${contact.nickname}\" · ${contact.messageType}",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(contact.phoneNumber, color = TextSecondary.copy(alpha = 0.5f), style = MaterialTheme.typography.labelSmall)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Delete", tint = NeonPink.copy(alpha = 0.7f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddVipContactDialog(
    phoneContacts: List<PhoneContact>,
    onDismiss: () -> Unit,
    onAdd: (VipContactEntity) -> Unit
) {
    var selectedContact by remember { mutableStateOf<PhoneContact?>(null) }
    var nickname by remember { mutableStateOf("") }
    var messageType by remember { mutableStateOf("FAMILY") }
    var customMessage by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var showContactPicker by remember { mutableStateOf(true) }

    val filteredContacts = remember(searchQuery, phoneContacts) {
        if (searchQuery.isBlank()) phoneContacts.take(50)
        else phoneContacts.filter { it.name.contains(searchQuery, ignoreCase = true) }.take(20)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SpaceBlack,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                if (selectedContact == null) "Pick a Contact" else "Configure VIP",
                color = NeonGold,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.heightIn(max = 500.dp)) {
                if (selectedContact == null) {
                    // Contact picker
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search contacts", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonGold,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(filteredContacts) { contact ->
                            TextButton(
                                onClick = { selectedContact = contact; nickname = contact.name.split(" ").firstOrNull() ?: "" },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(contact.name, color = TextPrimary, fontWeight = FontWeight.Medium)
                                    Text(contact.number, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                } else {
                    // Configuration form
                    Text("Contact: ${selectedContact!!.name}", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = nickname,
                        onValueChange = { nickname = it },
                        label = { Text("Nickname (e.g. Wife, Mom, Big Bob)", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonGold,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Message Type", color = TextSecondary, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("FAMILY", "CLIENT", "CUSTOM").forEach { type ->
                            FilterChip(
                                selected = messageType == type,
                                onClick = { messageType = type },
                                label = { Text(type, style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NeonGold.copy(alpha = 0.2f),
                                    selectedLabelColor = NeonGold
                                )
                            )
                        }
                    }
                    if (messageType == "CUSTOM") {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = customMessage,
                            onValueChange = { customMessage = it },
                            label = { Text("Custom message", color = TextSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = NeonGold,
                                unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (selectedContact != null) {
                Button(
                    onClick = {
                        val contact = selectedContact ?: return@Button
                        onAdd(
                            VipContactEntity(
                                phoneNumber = contact.number.filter { it.isDigit() || it == '+' },
                                contactName = contact.name,
                                nickname = nickname.ifBlank { contact.name },
                                messageType = messageType,
                                customMessage = customMessage
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGold)
                ) {
                    Text("Add VIP", color = SpaceBlack, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = {
                if (selectedContact != null) selectedContact = null else onDismiss()
            }) {
                Text(if (selectedContact != null) "Back" else "Cancel", color = TextSecondary)
            }
        }
    )
}
