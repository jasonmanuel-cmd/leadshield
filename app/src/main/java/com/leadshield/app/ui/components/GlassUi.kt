package com.leadshield.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.leadshield.app.ui.theme.NeonCyan
import com.leadshield.app.ui.theme.SurfaceBorder

/**
 * A premium Glassmorphism card for "Futuristic Elegance" aesthetic.
 * Uses semi-transparent background, subtle glow, and elegant gold/cyan accents.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp, // Slightly rounder for elegance
    onClick: (() -> Unit)? = null,
    glowColor: Color = Color.White.copy(alpha = 0.05f),
    borderColor: Color? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val cardModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }

    val borderBrush = if (borderColor != null) {
        Brush.linearGradient(colors = listOf(borderColor, borderColor.copy(alpha = 0.3f)))
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.3f),
                Color.White.copy(alpha = 0.05f),
                Color.White.copy(alpha = 0.15f)
            )
        )
    }

    Box(
        modifier = cardModifier
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = glowColor,
                spotColor = glowColor
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.12f),
                        Color.White.copy(alpha = 0.04f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(cornerRadius)
            )
    ) {
        Box(
            modifier = Modifier.padding(1.dp)
        ) {
            content()
        }
    }
}
