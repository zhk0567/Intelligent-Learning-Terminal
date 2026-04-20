package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.StoryFeedItem
import com.intangibleheritage.music.core.data.model.StoryFeedTab
import com.intangibleheritage.music.core.resources.R
import kotlinx.coroutines.delay

class FakeStoriesRepository : StoriesRepository {

    private val recommend = listOf(
        StoryFeedItem("s1", R.drawable.story_feed_01, null, 220),
        StoryFeedItem("s2", R.drawable.story_feed_02, R.string.story_overlay_fireworks, 160),
        StoryFeedItem("s3", R.drawable.story_feed_03, null, 200),
        StoryFeedItem("s4", R.drawable.story_feed_04, R.string.story_overlay_super, 140),
        StoryFeedItem("s5", R.drawable.story_feed_05, null, 240),
        StoryFeedItem("s6", R.drawable.story_feed_06, null, 150),
        StoryFeedItem("s7", R.drawable.story_feed_07, null, 190),
        StoryFeedItem("s8", R.drawable.story_feed_08, null, 170)
    )

    /** 关注流：示例数据较少，便于验证空状态与 Tab 差异 */
    private val following = listOf(
        StoryFeedItem("f1", R.drawable.story_feed_03, R.string.story_overlay_fireworks, 200),
        StoryFeedItem("f2", R.drawable.story_feed_05, null, 180),
        StoryFeedItem("f3", R.drawable.story_feed_07, R.string.story_overlay_super, 160)
    )

    override fun feed(tab: StoryFeedTab): List<StoryFeedItem> = when (tab) {
        StoryFeedTab.Recommend -> recommend
        StoryFeedTab.Following -> following
    }

    override fun storyById(id: String): StoryFeedItem? {
        val base = if (id.contains("_more_")) id.substringBefore("_more_") else id
        return recommend.find { it.id == base } ?: following.find { it.id == base }
    }

    override suspend fun refreshFeed(tab: StoryFeedTab): List<StoryFeedItem> {
        delay(650)
        return feed(tab)
    }

    override suspend fun loadMore(tab: StoryFeedTab, accumulated: List<StoryFeedItem>): List<StoryFeedItem> {
        delay(420)
        val base = feed(tab)
        val baseSize = base.size
        if (accumulated.size >= baseSize + 4) return emptyList()
        return base.take(2).mapIndexed { i, item ->
            item.copy(
                id = "${item.id}_more_${accumulated.size}_$i",
                heightDp = (item.heightDp * 0.9f).toInt().coerceIn(120, 280)
            )
        }
    }
}
