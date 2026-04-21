package com.intangibleheritage.music.core.data.model

/**
 * 「我的」页用户可编辑展示信息（[com.intangibleheritage.music.core.data.repository.ProfileRepository] / DataStore）。
 */
data class UserProfilePrefs(
    val nickname: String,
    /** 内置头像预设键，由 UI 映射为 drawable。 */
    val avatarKey: String,
    val themeKey: String
) {
    companion object {
        const val AvatarDefault = "default"
        const val AvatarPick1 = "pick1"
        const val AvatarPick2 = "pick2"
        const val AvatarPick3 = "pick3"
        const val ThemeTechDark = "theme_tech_dark"
        const val ThemePaperLight = "theme_paper_light"
        const val ThemeNeonPurpleBlue = "theme_neon_purple_blue"
        const val ThemeForestGold = "theme_forest_gold"

        val Default = UserProfilePrefs(
            nickname = "",
            avatarKey = AvatarDefault,
            themeKey = ThemeTechDark
        )
    }
}
