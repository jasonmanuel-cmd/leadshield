package com.leadshield.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.leadshield.app.ui.theme.NeonCyan
import com.leadshield.app.ui.theme.NeonPurple
import com.leadshield.app.ui.theme.SpaceBlack
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.unit.dp

/**
 * A futuristic animated background with moving gradients and subtle glow artifacts.
 * The "God Mode" aesthetic foundation.
 */
@Composable
fun AnimatedMeshBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "mesh")

    val phaseA by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phaseA"
    )

    val phaseB by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phaseB"
    )

    val twinklePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "twinkle"
    )

    val stars = remember {
        List(22) { index ->
            val x = ((index * 37) % 100) / 100f
            val y = ((index * 53 + 17) % 100) / 100f
            val radius = if (index % 3 == 0) 1.5f else 1f
            Triple(x, y, radius)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    SpaceBlack,
                    Color(0xFF080D17)
                ),
                start = Offset(0f, 0f),
                end = Offset(width, height)
            )
        )

        // Pulsing cyan gradient
        val center1 = Offset(
            x = width * (0.2f + 0.6f * phaseA),
            y = height * (0.25f + 0.5f * phaseB)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(NeonCyan.copy(alpha = 0.12f), Color.Transparent), // Reduced alpha for elegance
                center = center1,
                radius = width * 1.2f
            ),
            center = center1,
            radius = width * 1.2f
        )

        // Pulsing purple gradient
        val center2 = Offset(
            x = width * (0.75f - 0.45f * phaseB),
            y = height * (0.65f - 0.35f * phaseA)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(NeonPurple.copy(alpha = 0.10f), Color.Transparent), // Reduced alpha
                center = center2,
                radius = width * 1.0f
            ),
            center = center2,
            radius = width * 1.0f
        )

        // Moving sweep to add depth
        drawRect(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color.Transparent,
                    NeonPurple.copy(alpha = 0.07f),
                    Color.Transparent
                ),
                center = Offset(
                    x = width * (0.5f + 0.25f * sin(twinklePhase * 0.2f)),
                    y = height * (0.5f + 0.25f * cos(twinklePhase * 0.2f))
                )
            )
        )

        // Twinkling stars/tech artifacts
        stars.forEachIndexed { index, (x, y, radius) ->
            val twinkle = ((sin(twinklePhase + index) + 1f) / 2f)
            val alpha = 0.08f + (0.22f * twinkle)
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = radius.dp.toPx(),
                center = Offset(width * x, height * y)
            )
        }
    }
}
