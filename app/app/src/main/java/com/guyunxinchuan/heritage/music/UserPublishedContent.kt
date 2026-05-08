package com.guyunxinchuan.heritage.music

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 本地演示用：用户发布作品后同步到「创作社区」Feed 与「我的作品」列表。
 */
object UserPublishedContent {

    private var nextPostId = 10_000

    private val userPosts = mutableListOf<Post>()
    private val userMyWorks = mutableListOf<MyWorksActivity.MyWork>()
    private val hiddenDefaultIds = mutableSetOf<String>()

    private val defaultMyWorks: List<MyWorksActivity.MyWork> = listOf(
        MyWorksActivity.MyWork("1", "古琴演奏 - 高山流水", "2024-01-15", 128, 45, 892, "已发布"),
        MyWorksActivity.MyWork("2", "二胡独奏 - 二泉映月", "2024-01-10", 86, 32, 654, "已发布"),
        MyWorksActivity.MyWork("3", "琵琶曲 - 十面埋伏", "2024-01-05", 0, 0, 0, "草稿"),
        MyWorksActivity.MyWork("4", "古筝演奏 - 渔舟唱晚", "2023-12-28", 256, 78, 1523, "已发布")
    )

    fun myWorksDisplayList(): List<MyWorksActivity.MyWork> =
        userMyWorks + defaultMyWorks.filter { it.id !in hiddenDefaultIds }

    fun removeWork(id: String) {
        userMyWorks.removeAll { it.id == id }
        val postId = id.toIntOrNull()
        if (postId != null) {
            userPosts.removeAll { it.id == postId }
        }
        if (defaultMyWorks.any { it.id == id }) {
            hiddenDefaultIds.add(id)
        }
    }

    fun postsForCategory(category: String, seed: List<Post>): List<Post> {
        val mine = userPosts.filter { it.category == category }
        val mineIds = mine.map { it.id }.toSet()
        return mine + seed.filter { it.id !in mineIds }
    }

    fun publishWork(title: String, description: String, tagLabels: Set<String>) {
        val category = mapTagsToCategory(tagLabels)
        val id = nextPostId++
        val post = Post(
            id = id,
            title = title,
            content = description,
            category = category,
            author = "我",
            timeText = "刚刚",
            likeCount = 0,
            commentCount = 0
        )
        userPosts.add(0, post)

        val date = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date())
        val work = MyWorksActivity.MyWork(
            id = id.toString(),
            title = title,
            publishDate = date,
            likes = 0,
            comments = 0,
            views = 0,
            status = "已发布"
        )
        userMyWorks.add(0, work)
    }

    private fun mapTagsToCategory(tags: Set<String>): String {
        if (tags.isEmpty()) return "弦乐"
        if (tags.contains("电子")) return "电子"
        if (tags.contains("器乐")) return "管乐"
        return "弦乐"
    }
}
