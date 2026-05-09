package com.leadshield.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leadshield.app.data.AuditRepository
import com.leadshield.app.data.WeeklyReport

@Composable
fun WeeklyAuditDashboard(auditRepository: AuditRepository) {
    var report by remember { mutableStateOf<WeeklyReport?>(null) }
    val scrollState = rememberScrollState()
    
    val backgroundColor = Color(0xFF0A0A0A)
    val cardColor = Color(0xFF1A1A1A)
    val accentGreen = Color(0xFF00E676)
    val accentBlue = Color(0xFF2979FF)

    LaunchedEffect(Unit) {
        report = auditRepository.generateWeeklyReport()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "SYSTEM AUDIT v2.0",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            letterSpacing = 2.sp
        )
        
        Text(
            text = "SOVEREIGNTY REPORT",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = Color.White,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(32.dp))

        report?.let { r ->
            AnimatedVisibility(visible = true) {
                Column {
                    // THE TITAN GAUGE
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        TitanGauge(score = r.stabilityScore, label = "TITAN STABILITY")
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // SYSTEM STATUS CARD
                    StatusCard(r, cardColor)

                    Spacer(modifier = Modifier.height(24.dp))

                    // SECONDARY METRICS
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        MetricBox(
                            title = "COMM FLOW",
                            value = "${r.flowConsistency.toInt()}%",
                            modifier = Modifier.weight(1f),
                            color = accentBlue,
                            containerColor = cardColor
                        )
                        MetricBox(
                            title = "ENTROPY",
                            value = "${r.amygdalaHijackCount}",
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFFF5252),
                            containerColor = cardColor,
                            subtitle = "Reacts Flagged"
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // THE BRUTAL RECOMMENDATION
                    Text(
                        text = "ARCHITECT'S DIRECTIVE",
                        style = MaterialTheme.typography.labelMedium,
                        color = accentGreen,
                        letterSpacing = 1.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Surface(
                        color = cardColor,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = r.recommendation.uppercase(),
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 24.sp
                        )
                    }
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = accentGreen)
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun TitanGauge(score: Float, label: String) {
    val animatedProgress = animateFloatAsState(
        targetValue = score / 100f,
        animationSpec = tween(durationMillis = 1500),
        label = "gauge"
    )

    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(240.dp)) {
            // Background track
            drawCircle(
                color = Color.DarkGray.copy(alpha = 0.3f),
                radius = size.minDimension / 2,
                style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
            )

            // Progress arc
            drawArc(
                brush = Brush.sweepGradient(
                    0f to Color(0xFF2979FF),
                    0.5f to Color(0xFF00E676),
                    1f to Color(0xFF2979FF)
                ),
                startAngle = -90f,
                sweepAngle = animatedProgress.value * 360f,
                useCenter = false,
                style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(animatedProgress.value * 100).toInt()}%",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
fun StatusCard(report: WeeklyReport, color: Color) {
    Surface(
        color = color,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color(android.graphics.Color.parseColor(report.statusColorHex)))
            ) {}
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = report.status,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun MetricBox(
    title: String, 
    value: String, 
    modifier: Modifier = Modifier, 
    color: Color, 
    containerColor: Color,
    subtitle: String? = null
) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = color)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}
