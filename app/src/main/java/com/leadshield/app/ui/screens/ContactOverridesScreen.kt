package com.leadshield.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.leadshield.app.data.*
import androidx.compose.material.icons.filled.Delete
import com.leadshield.app.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactOverridesScreen(
    navController: NavController,
    repository: ContactRepository
) {
    val overrides by repository.getAllCustomMessages().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Contact Messages") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.ContactEditor.createRoute("new")) }) {
                Icon(Icons.Filled.Add, contentDescription = "Add New Override")
            }
        }
    ) { padding ->
        if (overrides.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "You haven't added any custom messages for contacts yet.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(overrides) { msg ->
                    ContactRow(
                        message = msg,
                        onClick = { navController.navigate(Screen.ContactEditor.createRoute(msg.phoneNumber)) },
                        onDelete = {
                            // Launch coroutine to delete
                            // repository.removeCustomMessage(msg.phoneNumber)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ContactRow(
    message: ContactMessage,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    ListItem(
        headlineContent = { Text(message.contactName ?: message.phoneNumber, fontWeight = FontWeight.SemiBold) },
        supportingContent = { Text(message.customMessage, maxLines = 1) },
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete")
            }
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
