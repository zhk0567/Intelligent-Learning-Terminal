package com.intangibleheritage.music

import com.intangibleheritage.music.core.data.AppRepositories

/**
 * 将「我的」收藏/历史中的 [com.intangibleheritage.music.core.data.model.ProfileGridItem.id]
 * 映射为根导航路由（P5-3 / 迭代 A 闭环）。
 */
internal object ProfileContentNav {

    private val productIds = setOf(
        "dunhuang_magnet",
        "bronze_bells",
        "silk_scarf",
        "pipa_bookmark"
    )

    /**
     * @return 可直接交给 [androidx.navigation.NavController.navigate] 的路由字符串。
     */
    fun routeFor(contentId: String): String = when {
        contentId.startsWith("track_") -> HeritageNavigation.player(contentId)
        contentId in productIds -> HeritageNavigation.product(contentId)
        AppRepositories.stories.storyById(contentId) != null -> HeritageNavigation.story(contentId)
        else -> HeritageNavigation.story(contentId)
    }
}
