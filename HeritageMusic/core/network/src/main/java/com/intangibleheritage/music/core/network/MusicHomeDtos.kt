package com.intangibleheritage.music.core.network

import com.google.gson.annotations.SerializedName

/**
 * 与后端约定的音乐馆首页 JSON；字段均可空以便渐进对接。
 */
data class MusicHomeResponseDto(
    @SerializedName("banners") val banners: List<BannerDto>? = null,
    @SerializedName("hotTiles") val hotTiles: List<HotTileDto>? = null,
    @SerializedName("dailyPicks") val dailyPicks: List<DailyPickDto>? = null,
    @SerializedName("guessTags") val guessTags: List<TagChipDto>? = null,
    @SerializedName("bottomCards") val bottomCards: List<HorizontalCardDto>? = null
)

data class BannerDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("audioTrackId") val audioTrackId: String? = null
)

data class HotTileDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("audioTrackId") val audioTrackId: String? = null
)

data class DailyPickDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("audioTrackId") val audioTrackId: String? = null
)

data class TagChipDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("label") val label: String? = null
)

data class HorizontalCardDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null
)
