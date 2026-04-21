package com.intangibleheritage.music

import android.net.Uri

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
    const val ROUTE_SETTINGS_THEME = "settings_theme"
    const val ROUTE_MUSIC_HALL_MORE_PREFIX = "music_hall_more/"
    const val ROUTE_MUSIC_HALL_TAG_PREFIX = "music_hall_tag/"
    const val ROUTE_ARCHIVE = "archive"
    const val ROUTE_ARCHIVE_DETAIL_PREFIX = "archive_detail/"
    const val ROUTE_COURSES = "courses"
    const val ROUTE_COURSE_DETAIL_PREFIX = "course_detail/"
    const val ROUTE_COURSE_OUTLINE_PREFIX = "course_outline/"
    const val ROUTE_COURSE_MATERIALS_PREFIX = "course_materials/"
    const val ROUTE_COURSE_TUTOR_PREFIX = "course_tutor/"
    const val ROUTE_INTERACTIVE = "interactive"
    const val ROUTE_COMPOSITION = "composition"
    const val ROUTE_MENTOR_REVIEW = "mentor_review"
    const val ROUTE_MALL_SECTION_PREFIX = "mall_section/"

    val secondaryRoutePrefixes: List<String> = listOf(
        ROUTE_PRODUCT_PREFIX,
        ROUTE_PLAYER_PREFIX,
        ROUTE_STORY_PREFIX,
        ROUTE_COMMUNITY_POST_PREFIX,
        ROUTE_MUSIC_HALL_MORE_PREFIX,
        ROUTE_MUSIC_HALL_TAG_PREFIX,
        ROUTE_ARCHIVE_DETAIL_PREFIX,
        ROUTE_COURSE_DETAIL_PREFIX,
        ROUTE_COURSE_OUTLINE_PREFIX,
        ROUTE_COURSE_MATERIALS_PREFIX,
        ROUTE_COURSE_TUTOR_PREFIX,
        ROUTE_MALL_SECTION_PREFIX
    )

    fun product(productId: String): String = "${ROUTE_PRODUCT_PREFIX}$productId"

    fun player(trackId: String): String = "${ROUTE_PLAYER_PREFIX}$trackId"

    fun story(storyId: String): String = "${ROUTE_STORY_PREFIX}$storyId"

    fun communityPost(postId: String): String = "${ROUTE_COMMUNITY_POST_PREFIX}$postId"

    fun musicHallMore(sectionTitle: String): String =
        "${ROUTE_MUSIC_HALL_MORE_PREFIX}${Uri.encode(sectionTitle)}"

    fun musicHallTag(tagName: String): String =
        "${ROUTE_MUSIC_HALL_TAG_PREFIX}${Uri.encode(tagName)}"

    fun archiveDetail(assetId: String): String = "${ROUTE_ARCHIVE_DETAIL_PREFIX}$assetId"
    fun courseDetail(courseId: String): String = "${ROUTE_COURSE_DETAIL_PREFIX}$courseId"
    fun courseOutline(courseId: String): String = "${ROUTE_COURSE_OUTLINE_PREFIX}$courseId"
    fun courseMaterials(courseId: String): String = "${ROUTE_COURSE_MATERIALS_PREFIX}$courseId"
    fun courseTutor(courseId: String): String = "${ROUTE_COURSE_TUTOR_PREFIX}$courseId"

    fun mallSection(sectionRouteKey: String): String = "${ROUTE_MALL_SECTION_PREFIX}$sectionRouteKey"

    fun hidesBottomBar(route: String): Boolean =
        secondaryRoutePrefixes.any { route.startsWith(it) } ||
            route == ROUTE_NOTIFICATIONS ||
            route == ROUTE_ABOUT ||
            route == ROUTE_LICENSES ||
            route == ROUTE_COMPOSE ||
            route == ROUTE_SETTINGS ||
            route == ROUTE_SETTINGS_NOTIFICATIONS ||
            route == ROUTE_SETTINGS_PRIVACY ||
            route == ROUTE_SETTINGS_THEME ||
            route == ROUTE_ARCHIVE ||
            route == ROUTE_COURSES ||
            route == ROUTE_INTERACTIVE ||
            route == ROUTE_COMPOSITION ||
            route == ROUTE_MENTOR_REVIEW
}
