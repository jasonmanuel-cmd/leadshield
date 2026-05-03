package com.mctb.autoreply.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mctb.autoreply.data.AuditEntry
import com.mctb.autoreply.data.AuditRepository
import com.mctb.autoreply.service.CipherService
import kotlinx.coroutines.launch

@Composable
fun SovereignDiaryScreen(cipherService: CipherService, auditRepository: AuditRepository) {
    var diaryText by remember { mutableStateOf("") }
    var cipherResponse by remember { mutableStateOf("THE CIPHER IS SILENT. WAITING FOR DATA INJECTION...") }
    var isAnalyzing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val terminalGreen = Color(0xFF00FF41)
    val terminalBlack = Color(0xFF0D0D0D)

    // Pulse animation for the glowing text
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(terminalBlack)
            .padding(24.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "SOVEREIGN DIARY // CIPHER v4.1",
            style = MaterialTheme.typography.labelMedium,
            color = terminalGreen,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 2.sp,
            modifier = Modifier.alpha(alpha)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // THE TERMINAL INPUT
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(Color.Black)
                .border(1.dp, terminalGreen.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
        ) {
            TextField(
                value = diaryText,
                onValueChange = { diaryText = it },
                modifier = Modifier.fillMaxSize(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    cursorColor = terminalGreen,
                    focusedTextColor = terminalGreen,
                    unfocusedTextColor = terminalGreen.copy(alpha = 0.8f)
                ),
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                ),
                placeholder = {
                    Text(
                        "ENTER THOUGHT STREAM...",
                        color = terminalGreen.copy(alpha = 0.3f),
                        fontFamily = FontFamily.Monospace
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ACTION BUTTON
        Button(
            onClick = {
                if (diaryText.isBlank()) return@Button
                scope.launch {
                    isAnalyzing = true
                    cipherResponse = "ENCRYPTING DATA... CONNECTING TO NEURAL NET..."
                    val response = cipherService.consultTheCipher(diaryText) ?: "CIPHER_ERROR: DATA STREAM INTERRUPTED"
                    cipherResponse = response
                    
                    val isPfcStable = response.contains("[PFC STABLE]", ignoreCase = true)
                    auditRepository.insert(
                        AuditEntry(
                            isPfcStable = isPfcStable,
                            moodIntensity = if (isPfcStable) 3 else 8 // Heuristic
                        )
                    )
                    isAnalyzing = false
                }
            },
            enabled = !isAnalyzing,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = terminalGreen.copy(alpha = 0.1f),
                contentColor = terminalGreen
            )
        ) {
            Text(
                if (isAnalyzing) "ANALYZING..." else "CONSULT THE CIPHER",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // THE CIPHER OUTPUT
        Text(
            text = "OUTPUT_LOG:",
            style = MaterialTheme.typography.labelSmall,
            color = terminalGreen.copy(alpha = 0.6f),
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            color = terminalGreen.copy(alpha = 0.05f),
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, terminalGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = cipherResponse,
                    color = terminalGreen,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
                
                if (isAnalyzing) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        color = terminalGreen,
                        trackColor = terminalGreen.copy(alpha = 0.1f)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}
