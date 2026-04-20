package com.intangibleheritage.music.core.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private fun heritageDarkScheme(): ColorScheme = darkColorScheme(
    primary = PrimaryTeal,
    onPrimary = Color(0xFF031018),
    primaryContainer = PrimaryTealDim,
    secondary = AccentViolet,
    onSecondary = Color(0xFFF5F0FF),
    tertiary = AccentViolet,
    onTertiary = Color(0xFF1A1030),
    background = Background,
    onBackground = OnBackground,
    surface = SurfaceDark,
    onSurface = OnBackground,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = OnBackgroundMuted,
    outline = PrimaryTeal.copy(alpha = 0.45f),
    outlineVariant = OnBackgroundMuted.copy(alpha = 0.28f)
)

@Composable
fun HeritageMusicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = heritageDarkScheme()
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
            window.statusBarColor = Background.toArgb()
            window.navigationBarColor = NavigationBarBg.toArgb()
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = HeritageTypography,
        content = content
    )
}
