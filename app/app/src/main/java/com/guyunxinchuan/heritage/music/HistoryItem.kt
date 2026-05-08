package com.guyunxinchuan.heritage.music

data class HistoryItem(
    val id: String,
    val title: String,
    val type: String, // "商品" 或 "作品"
    val imageResId: Int,
    val browseTime: String
)
