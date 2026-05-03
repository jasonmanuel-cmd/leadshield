package com.mctb.autoreply.ui.screens

import android.telephony.SmsManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mctb.autoreply.ui.components.AnimatedMeshBackground
import com.mctb.autoreply.ui.components.GlassCard
import com.mctb.autoreply.ui.navigation.Screen
import com.mctb.autoreply.ui.theme.NeonCyan
import com.mctb.autoreply.ui.theme.NeonGold
import com.mctb.autoreply.ui.theme.SpaceBlack
import com.mctb.autoreply.ui.theme.TextPrimary
import com.mctb.autoreply.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    navController: NavController,
    phoneNumber: String
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var jobDescription by remember { mutableStateOf("") }
    var appointmentDate by remember { mutableStateOf("") }
    var depositAmount by remember { mutableStateOf("") }

    val previewMessage = buildString {
        val name = if (phoneNumber.isNotBlank()) phoneNumber else "Customer"
        val date = if (appointmentDate.isNotBlank()) appointmentDate else "[date]"
        val amount = if (depositAmount.isNotBlank()) depositAmount else "[amount]"
        append("Hey $name, your appointment is confirmed for $date. ")
        append("Secure your spot with a \$$amount deposit: [STRIPE LINK WILL APPEAR HERE]")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Book Job + Collect Deposit",
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
        AnimatedMeshBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            val fieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = TextSecondary.copy(alpha = 0.4f),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedLabelColor = NeonCyan,
                unfocusedLabelColor = TextSecondary,
                cursorColor = NeonCyan,
                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.03f)
            )

            // Job Description
            OutlinedTextField(
                value = jobDescription,
                onValueChange = { jobDescription = it },
                label = { Text("Job Description") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Appointment Date
            OutlinedTextField(
                value = appointmentDate,
                onValueChange = { appointmentDate = it },
                label = { Text("Appointment Date") },
                placeholder = { Text("e.g. Tuesday, June 3rd, 2–4pm", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Deposit Amount
            OutlinedTextField(
                value = depositAmount,
                onValueChange = { depositAmount = it },
                label = { Text("Deposit Amount ($)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Customer Phone (display only)
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {},
                label = { Text("Customer Phone") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = TextSecondary.copy(alpha = 0.3f),
                    disabledTextColor = TextSecondary,
                    disabledLabelColor = TextSecondary,
                    disabledContainerColor = Color.White.copy(alpha = 0.02f)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Message Preview
            Text(
                text = "Message Preview",
                style = MaterialTheme.typography.labelMedium,
                color = NeonCyan,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16.dp) {
                Text(
                    text = previewMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Send booking + payment link button
            Button(
                onClick = {
                    clipboardManager.setText(AnnotatedString(previewMessage))
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            "Stripe integration coming soon — tap to copy message manually"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Send Booking + Payment Link",
                    color = SpaceBlack,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Skip deposit — just confirm
            TextButton(
                onClick = {
                    val confirmMsg = buildString {
                        val date = if (appointmentDate.isNotBlank()) appointmentDate else "your appointment"
                        append("Hey! Your appointment is confirmed for $date. We'll see you then!")
                    }
                    try {
                        val smsManager = SmsManager.getDefault()
                        smsManager.sendTextMessage(phoneNumber, null, confirmMsg, null, null)
                    } catch (e: Exception) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Could not send SMS: ${e.message}")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Skip deposit, just confirm appointment",
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
