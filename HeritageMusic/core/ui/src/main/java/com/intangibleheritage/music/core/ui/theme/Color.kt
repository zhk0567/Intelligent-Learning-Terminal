package com.intangibleheritage.music.core.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

/** 深空底 + 电青主色，偏 HUD / 数字舞台 */
private object LegacyThemeBridge {
    var background by mutableStateOf(Color(0xFF050814))
    var backgroundElevated by mutableStateOf(Color(0xFF0C1224))
    var surfaceDark by mutableStateOf(Color(0xFF0E1628))
    var surfaceCard by mutableStateOf(Color(0xFF121C32))
    var primaryTeal by mutableStateOf(Color(0xFF22D3EE))
    var primaryTealDim by mutableStateOf(Color(0xFF0D4F5C))
    var accentViolet by mutableStateOf(Color(0xFF8B5CF6))
    var onBackground by mutableStateOf(Color(0xFFE8F4FF))
    var onBackgroundMuted by mutableStateOf(Color(0xFF8BA3B8))
    var borderTeal by mutableStateOf(Color(0xFF22D3EE))
    var navigationBarBg by mutableStateOf(Color(0xFF060A14))
    var techGridLine by mutableStateOf(Color(0x3322D3EE))
    var techGlow by mutableStateOf(Color(0x6622D3EE))
}

val Background: Color get() = LegacyThemeBridge.background
val BackgroundElevated: Color get() = LegacyThemeBridge.backgroundElevated
val SurfaceDark: Color get() = LegacyThemeBridge.surfaceDark
val SurfaceCard: Color get() = LegacyThemeBridge.surfaceCard
val PrimaryTeal: Color get() = LegacyThemeBridge.primaryTeal
val PrimaryTealDim: Color get() = LegacyThemeBridge.primaryTealDim
val AccentViolet: Color get() = LegacyThemeBridge.accentViolet
val OnBackground: Color get() = LegacyThemeBridge.onBackground
val OnBackgroundMuted: Color get() = LegacyThemeBridge.onBackgroundMuted
val BorderTeal: Color get() = LegacyThemeBridge.borderTeal
val NavigationBarBg: Color get() = LegacyThemeBridge.navigationBarBg
val TechGridLine: Color get() = LegacyThemeBridge.techGridLine
val TechGlow: Color get() = LegacyThemeBridge.techGlow

internal fun updateLegacyThemeBridge(
    colorScheme: ColorScheme,
    themeKey: String
) {
    LegacyThemeBridge.background = colorScheme.background
    LegacyThemeBridge.backgroundElevated = colorScheme.surface
    LegacyThemeBridge.surfaceDark = colorScheme.surface
    LegacyThemeBridge.surfaceCard = colorScheme.surfaceVariant
    LegacyThemeBridge.primaryTeal = colorScheme.primary
    LegacyThemeBridge.primaryTealDim = colorScheme.primaryContainer
    LegacyThemeBridge.accentViolet = colorScheme.secondary
    LegacyThemeBridge.onBackground = colorScheme.onBackground
    LegacyThemeBridge.onBackgroundMuted = colorScheme.onSurfaceVariant
    LegacyThemeBridge.borderTeal = colorScheme.outline
    LegacyThemeBridge.navigationBarBg = colorScheme.surface

    val techBase = when (themeKey) {
        HeritageThemeKeys.PaperLight -> colorScheme.secondary
        HeritageThemeKeys.NeonPurpleBlue -> colorScheme.tertiary
        HeritageThemeKeys.ForestGold -> colorScheme.primary
        else -> colorScheme.primary
    }
    LegacyThemeBridge.techGridLine = techBase.copy(alpha = 0.22f)
    LegacyThemeBridge.techGlow = techBase.copy(alpha = 0.38f)
}
