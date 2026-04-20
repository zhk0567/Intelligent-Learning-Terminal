package com.intangibleheritage.music

/**
 * 应用级导航约定（P0-2），与 [HeritageApp] 中 `NavHost` 保持一致。
 *
 * **底栏显示**：五个主 Tab（`music_hall`、`stories`、`community`、`mall`、`profile`）显示底栏；
 * 凡 [secondaryRoutePrefixes] 中前缀匹配当前 `destination.route` 时隐藏底栏（全屏二级页）。
 * 已包含 `story/`、`community_post/`；另有 `notifications`、`about`、`licenses`、`compose`、`settings` 及 `settings_*` 子页等全屏页亦隐藏底栏。
 *
 * **返回栈与 Tab 切换**：五个主 Tab 在单一路由 [ROUTE_MAIN_TABS] 内用 `HorizontalPager` 承载，切换 Tab 不经过 NavHost 换页，
 * 避免整页销毁重建；从二级页返回时仍回到离开前的 Tab（由 Pager 状态保存）。
 *
 * **非法参数**：`product/{id}`、`player/{id}` 在数据层无对应项时，由各自 Screen 使用 [com.intangibleheritage.music.core.ui.navigation.InvalidDeepLinkScreen] 展示说明并保留顶栏返回（P0-1）。
 */
internal object HeritageNavigation {

    /** 底栏五 Tab 共用此路由，内部用 HorizontalPager 切换，勿与旧版各 Tab 独立路由混用。 */
    const val ROUTE_MAIN_TABS = "main_tabs"

    const val ROUTE_PRODUCT_PREFIX = "product/"
    const val ROUTE_PLAYER_PREFIX = "player/"
    const val ROUTE_STORY_PREFIX = "story/"
    const val ROUTE_COMMUNITY_POST_PREFIX = "community_post/"
    const val ROUTE_NOTIFICATIONS = "notifications"
    const val ROUTE_ABOUT = "about"
    const val ROUTE_LICENSES = "licenses"
    const val ROUTE_COMPOSE = "compose"
    const val ROUTE_SETTINGS = "settings"
    const val ROUTE_SETTINGS_NOTIFICATIONS = "settings_notifications"
    const val ROUTE_SETTINGS_PRIVACY = "settings_privacy"

    val secondaryRoutePrefixes: List<String> = listOf(
        ROUTE_PRODUCT_PREFIX,
        ROUTE_PLAYER_PREFIX,
        ROUTE_STORY_PREFIX,
        ROUTE_COMMUNITY_POST_PREFIX
    )

    fun product(productId: String): String = "${ROUTE_PRODUCT_PREFIX}$productId"

    fun player(trackId: String): String = "${ROUTE_PLAYER_PREFIX}$trackId"

    fun story(storyId: String): String = "${ROUTE_STORY_PREFIX}$storyId"

    fun communityPost(postId: String): String = "${ROUTE_COMMUNITY_POST_PREFIX}$postId"

    fun hidesBottomBar(route: String): Boolean =
        secondaryRoutePrefixes.any { route.startsWith(it) } ||
            route == ROUTE_NOTIFICATIONS ||
            route == ROUTE_ABOUT ||
            route == ROUTE_LICENSES ||
            route == ROUTE_COMPOSE ||
            route == ROUTE_SETTINGS ||
            route == ROUTE_SETTINGS_NOTIFICATIONS ||
            route == ROUTE_SETTINGS_PRIVACY
}
