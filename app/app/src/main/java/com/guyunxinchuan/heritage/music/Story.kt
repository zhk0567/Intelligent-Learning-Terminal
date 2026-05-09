package com.guyunxinchuan.heritage.music

data class Story(
    val id: Any,  // 支持 Int 或 String
    val title: String,
    val author: String = "非遗传承人",
    val category: String = "推荐",
    /** 无封面图时占位块颜色（`R.color.story_mock_tile_*`），运行时经 [SkinResources] 解析 */
    val placeholderColorRes: Int = R.color.story_mock_tile_1,
    val aspectRatio: Float = 1.2f,
    val imageResId: Int = -1,
    // 新增字段（用于 FavoriteActivity）
    val content: String = "",
    val imageUrl: String = "",
    val videoUrl: String? = null,
    val publishDate: Long = System.currentTimeMillis(),
    val tags: List<String> = listOf(),
    // 新增字段（用于 StoryListActivity）
    val publishTime: String = "",
    /** 与 `StoriesData` / 源 PNG 一致，用于列表按真实长宽比占位 */
    val coverWidth: Int = 0,
    val coverHeight: Int = 0,
    val coverResId: Int = -1,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val readCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false
)
