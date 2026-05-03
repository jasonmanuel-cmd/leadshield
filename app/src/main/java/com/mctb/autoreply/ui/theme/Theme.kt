package com.mctb.autoreply.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

// ============================================
// SPACE AGE DARK COLOR SCHEME
// ============================================
private val SpaceAgeDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = SpaceBlack,
    primaryContainer = NeonCyanDark,
    onPrimaryContainer = NeonCyanLight,
    secondary = NeonPurple,
    onSecondary = SpaceBlack,
    secondaryContainer = Color(0xFF7C4DFF),
    onSecondaryContainer = Color(0xFFFFFFFF),
    tertiary = NeonBlue,
    onTertiary = SpaceBlack,
    tertiaryContainer = Color(0xFF0091EA),
    onTertiaryContainer = Color(0xFFFFFFFF),
    error = ErrorRed,
    errorContainer = Color(0xFFD32F2F),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFFFFCDD2),
    background = SpaceBlack,
    onBackground = TextPrimary,
    surface = SpaceBlackCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceBorder,
)

// ============================================
// LIGHT COLOR SCHEME (Space Age Style)
// ============================================
private val SpaceAgeLightColorScheme = lightColorScheme(
    primary = NeonCyan,
    onPrimary = SpaceBlack,
    primaryContainer = NeonCyanDark,
    onPrimaryContainer = NeonCyanLight,
    secondary = NeonPurple,
    onSecondary = SpaceBlack,
    secondaryContainer = Color(0xFF7C4DFF),
    onSecondaryContainer = Color(0xFFFFFFFF),
    tertiary = NeonBlue,
    onTertiary = SpaceBlack,
    tertiaryContainer = Color(0xFF0091EA),
    onTertiaryContainer = Color(0xFFFFFFFF),
    error = ErrorRed,
    errorContainer = Color(0xFFD32F2F),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFFFFCDD2),
    background = SpaceBlack,
    onBackground = TextPrimary,
    surface = SpaceBlackCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceBorder,
)

@Composable
fun AppTheme(
    // Always use dark theme for space-age aesthetic
    darkTheme: Boolean = true,
    // Disable dynamic colors to maintain consistent space-age look
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Always use our custom space-age color scheme
    val colorScheme = SpaceAgeDarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

