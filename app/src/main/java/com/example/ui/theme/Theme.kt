package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = Color.White,
    primaryContainer = PurpleSoftLavender,
    onPrimaryContainer = PurpleDeep,
    secondary = PurpleDeep,
    onSecondary = Color.White,
    secondaryContainer = PurpleSoftLavender,
    onSecondaryContainer = PurpleDeep,
    tertiary = MarketGreen,
    onTertiary = Color.White,
    background = Color(0xFFFAF9FD),
    onBackground = TextPrimary,
    surface = Color.White,
    onSurface = TextPrimary,
    surfaceVariant = PurpleSurfaceLight,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    outlineVariant = Color(0xFFE5E0F2)
)

private val DarkColorScheme = darkColorScheme(
    primary = PurplePrimary,
    onPrimary = Color.White,
    primaryContainer = PurpleDeep,
    onPrimaryContainer = PurpleSoftLavender,
    secondary = PurpleSoftLavender,
    onSecondary = PurpleDeep,
    tertiary = MarketGreen,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = Color(0xFFF3F1FA),
    surface = DarkSurface,
    onSurface = Color(0xFFF3F1FA),
    surfaceVariant = Color(0xFF221E33),
    onSurfaceVariant = Color(0xFFAFAABF),
    outline = Color(0xFF352F4D),
    outlineVariant = Color(0xFF2B2540)
)

@Composable
fun Stock1Theme(
    darkTheme: Boolean = false, // Default to crisp Apple-inspired white & purple
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
