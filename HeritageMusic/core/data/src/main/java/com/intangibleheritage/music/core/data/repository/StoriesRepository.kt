package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.StoryFeedItem
import com.intangibleheritage.music.core.data.model.StoryFeedTab

interface StoriesRepository {
    fun feed(tab: StoryFeedTab): List<StoryFeedItem>

    /** 推荐与关注流合并查找，供详情页使用 */
    fun storyById(id: String): StoryFeedItem?

    /** 模拟网络刷新；返回与 [feed] 同构的最新列表 */
    suspend fun refreshFeed(tab: StoryFeedTab): List<StoryFeedItem>

    /** 瀑布流分页追加；无更多数据时返回空列表 */
    suspend fun loadMore(tab: StoryFeedTab, accumulated: List<StoryFeedItem>): List<StoryFeedItem>
}
