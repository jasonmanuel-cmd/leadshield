package com.leadshield.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.leadshield.app.data.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactEditorScreen(
    navController: NavController,
    phoneNumber: String, // "new" for new entry
    repository: ContactRepository
) {
    var phone by remember { mutableStateOf(if (phoneNumber == "new") "" else phoneNumber) }
    var name by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Load existing?
    LaunchedEffect(phoneNumber) {
        if (phoneNumber != "new") {
            // repository.getEntrySync is needed here
            // But let's simplify for now
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (phoneNumber == "new") "Add Custom Context" else "Edit Override") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                enabled = phoneNumber == "new"
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Contact Name (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Custom Auto-Reply Message") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    scope.launch {
                        repository.updateCustomMessage(phone, message, name)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = phone.isNotBlank() && message.isNotBlank()
            ) {
                Text("Save Changes")
            }
        }
    }
}
