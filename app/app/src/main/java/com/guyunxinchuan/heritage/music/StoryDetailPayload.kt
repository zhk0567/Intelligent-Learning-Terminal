package com.guyunxinchuan.heritage.music

import android.content.Intent

/**
 * 故事详情展示数据（与 Intent / 未来 JSON 字段一一对应，便于替换为接口层）。
 */
data class StoryDetailPayload(
    val id: String,
    val title: String,
    val category: String,
    val author: String,
    val publishedAt: String,
    /** 正文；后端可单独字段返回 */
    val body: String,
    /** 可选导读/摘要；无则 UI 不展示导读块，避免与正文重复 */
    val summary: String?,
    val coverResId: Int,
    val tags: List<String>,
    val readCount: Int,
    val likeCount: Int,
    val commentCount: Int,
) {
    companion object {
        fun fromIntent(intent: Intent): StoryDetailPayload {
            val id = intent.getStringExtra(Extras.ID).orEmpty()
            val title = intent.getStringExtra(Extras.TITLE).orEmpty()
            val category = intent.getStringExtra(Extras.CATEGORY).orEmpty()
            val author = intent.getStringExtra(Extras.AUTHOR).orEmpty()
            val publishedAt = intent.getStringExtra(Extras.PUBLISHED_AT).orEmpty()
            val body = intent.getStringExtra(Extras.BODY).orEmpty()
            val summaryExtra = intent.getStringExtra(Extras.SUMMARY)?.trim()?.takeIf { it.isNotEmpty() }
            val cover = intent.getIntExtra(Extras.COVER, R.drawable.p_1)
            val tags = intent.getStringExtra(Extras.TAGS)
                ?.split("|||")
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                .orEmpty()
            return StoryDetailPayload(
                id = id,
                title = title,
                category = category,
                author = author,
                publishedAt = publishedAt,
                body = body,
                summary = summaryExtra,
                coverResId = if (cover != 0) cover else R.drawable.p_1,
                tags = tags,
                readCount = intent.getIntExtra(Extras.READ_COUNT, 0),
                likeCount = intent.getIntExtra(Extras.LIKE_COUNT, 0),
                commentCount = intent.getIntExtra(Extras.COMMENT_COUNT, 0),
            )
        }
    }

    object Extras {
        const val ID = "story_id"
        const val TITLE = "story_title"
        const val AUTHOR = "story_author"
        const val PUBLISHED_AT = "story_time"
        const val BODY = "story_content"
        const val SUMMARY = "story_summary"
        const val COVER = "story_cover"
        const val CATEGORY = "story_category"
        const val TAGS = "story_tags"
        const val READ_COUNT = "read_count"
        const val LIKE_COUNT = "like_count"
        const val COMMENT_COUNT = "comment_count"
    }
}
