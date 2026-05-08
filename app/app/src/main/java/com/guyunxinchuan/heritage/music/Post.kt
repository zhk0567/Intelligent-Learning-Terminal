package com.guyunxinchuan.heritage.music

data class Post(
    val id: Int,
    val title: String,
    val content: String,
    val category: String,
    val author: String = "匿名用户",
    val timeText: String = "2 小时前",
    val likeCount: Int = 128,
    val commentCount: Int = 32
)
