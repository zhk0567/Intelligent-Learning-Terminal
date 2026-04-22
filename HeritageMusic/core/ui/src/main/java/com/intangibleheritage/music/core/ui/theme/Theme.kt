package com.intangibleheritage.music.core.ui.theme

import android.app.Activity
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

object HeritageThemeKeys {
    const val TechDark = "theme_tech_dark"
    const val PaperLight = "theme_paper_light"
    const val NeonPurpleBlue = "theme_neon_purple_blue"
    const val ForestGold = "theme_forest_gold"
}

private fun heritageDarkScheme(): ColorScheme = darkColorScheme(
    primary = Color(0xFF37E7FF),
    onPrimary = Color(0xFF031018),
    primaryContainer = Color(0xFF0F5A67),
    secondary = Color(0xFFA88BFF),
    onSecondary = Color(0xFFF5F0FF),
    tertiary = Color(0xFF9C7BFF),
    onTertiary = Color(0xFF1A1030),
    background = Color(0xFF050814),
    onBackground = Color(0xFFEAF6FF),
    surface = Color(0xFF0F182B),
    onSurface = Color(0xFFEAF6FF),
    surfaceVariant = Color(0xFF17243B),
    onSurfaceVariant = Color(0xFFA9BED0),
    outline = Color(0xFF4D8AA0),
    outlineVariant = Color(0xFF355769)
)

private fun paperLightScheme(): ColorScheme = lightColorScheme(
    primary = Color(0xFF2A496E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCDDDF2),
    secondary = Color(0xFF845A2A),
    onSecondary = Color.White,
    tertiary = Color(0xFF5B6A43),
    onTertiary = Color.White,
    background = Color(0xFFF7F2E8),
    onBackground = Color(0xFF252525),
    surface = Color(0xFFFFFCF4),
    onSurface = Color(0xFF252525),
    surfaceVariant = Color(0xFFE7DECB),
    onSurfaceVariant = Color(0xFF4D473C),
    outline = Color(0xFF726853),
    outlineVariant = Color(0xFF9E9278)
)

private fun neonPurpleBlueScheme(): ColorScheme = darkColorScheme(
    primary = Color(0xFF67DFEC),
    onPrimary = Color(0xFF00262C),
    primaryContainer = Color(0xFF055B67),
    secondary = Color(0xFFB28AF3),
    onSecondary = Color(0xFF26124F),
    tertiary = Color(0xFFF154C6),
    onTertiary = Color(0xFF4C0038),
    background = Color(0xFF0D0218),
    onBackground = Color(0xFFF2E9FF),
    surface = Color(0xFF1A042F),
    onSurface = Color(0xFFF2E9FF),
    surfaceVariant = Color(0xFF260A40),
    onSurfaceVariant = Color(0xFFD1B8EE),
    outline = Color(0xFF865BCF),
    outlineVariant = Color(0xFF593C86)
)

private fun forestGoldScheme(): ColorScheme = darkColorScheme(
    primary = Color(0xFFE8C867),
    onPrimary = Color(0xFF2B2100),
    primaryContainer = Color(0xFF725900),
    secondary = Color(0xFF58CD80),
    onSecondary = Color(0xFF062D15),
    tertiary = Color(0xFF8FE6B6),
    onTertiary = Color(0xFF103321),
    background = Color(0xFF101C16),
    onBackground = Color(0xFFE9F3EC),
    surface = Color(0xFF182C21),
    onSurface = Color(0xFFE9F3EC),
    surfaceVariant = Color(0xFF21382B),
    onSurfaceVariant = Color(0xFFB4CBBB),
    outline = Color(0xFF7CA389),
    outlineVariant = Color(0xFF425E4C)
)

@Composable
fun HeritageMusicTheme(
    themeKey: String = HeritageThemeKeys.TechDark,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeKey) {
        HeritageThemeKeys.PaperLight -> paperLightScheme()
        HeritageThemeKeys.NeonPurpleBlue -> neonPurpleBlueScheme()
        HeritageThemeKeys.ForestGold -> forestGoldScheme()
        else -> heritageDarkScheme()
    }
    updateLegacyThemeBridge(colorScheme = colorScheme, themeKey = themeKey)
    val useLightSystemBars = themeKey == HeritageThemeKeys.PaperLight
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = useLightSystemBars
                isAppearanceLightNavigationBars = useLightSystemBars
            }
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = HeritageTypography,
        content = content
    )
}
