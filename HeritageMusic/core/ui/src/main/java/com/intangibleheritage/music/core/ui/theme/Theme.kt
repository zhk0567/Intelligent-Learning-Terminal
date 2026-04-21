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

private fun paperLightScheme(): ColorScheme = lightColorScheme(
    primary = Color(0xFF2E4A6B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDE8F5),
    secondary = Color(0xFF8A5A2B),
    onSecondary = Color.White,
    tertiary = Color(0xFF5E6D45),
    onTertiary = Color.White,
    background = Color(0xFFF4EFE2),
    onBackground = Color(0xFF2A2A2A),
    surface = Color(0xFFFFFAF0),
    onSurface = Color(0xFF2A2A2A),
    surfaceVariant = Color(0xFFE9E1CF),
    onSurfaceVariant = Color(0xFF5F594C),
    outline = Color(0xFF867C67),
    outlineVariant = Color(0xFFB4A98F)
)

private fun neonPurpleBlueScheme(): ColorScheme = darkColorScheme(
    primary = Color(0xFF6CF0FF),
    onPrimary = Color(0xFF002329),
    primaryContainer = Color(0xFF00606D),
    secondary = Color(0xFFB68CFF),
    onSecondary = Color(0xFF220F4D),
    tertiary = Color(0xFFFF5CD1),
    onTertiary = Color(0xFF4C0038),
    background = Color(0xFF090012),
    onBackground = Color(0xFFF2E9FF),
    surface = Color(0xFF17002B),
    onSurface = Color(0xFFF2E9FF),
    surfaceVariant = Color(0xFF23003D),
    onSurfaceVariant = Color(0xFFCCB2EE),
    outline = Color(0xFF8D5BDA),
    outlineVariant = Color(0xFF5E3A90)
)

private fun forestGoldScheme(): ColorScheme = darkColorScheme(
    primary = Color(0xFFD9B64A),
    onPrimary = Color(0xFF2B2100),
    primaryContainer = Color(0xFF5C4700),
    secondary = Color(0xFF54C77B),
    onSecondary = Color(0xFF062D15),
    tertiary = Color(0xFF89E0B0),
    onTertiary = Color(0xFF103321),
    background = Color(0xFF0E1A14),
    onBackground = Color(0xFFE9F3EC),
    surface = Color(0xFF15271D),
    onSurface = Color(0xFFE9F3EC),
    surfaceVariant = Color(0xFF1E3327),
    onSurfaceVariant = Color(0xFFAFC6B8),
    outline = Color(0xFF6B8F79),
    outlineVariant = Color(0xFF3C5948)
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
