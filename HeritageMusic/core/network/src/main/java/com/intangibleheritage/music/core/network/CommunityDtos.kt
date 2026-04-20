package com.intangibleheritage.music.core.network

import com.google.gson.annotations.SerializedName

data class CommunityPostsResponseDto(
    @SerializedName("posts") val posts: List<CommunityPostDto>? = null
)

data class CommunityPostDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("subtitle") val subtitle: String? = null,
    @SerializedName("body") val body: String? = null,
    /** 与客户端枚举名一致：`FolkInstrument` / `Electronic` / `Ai`（大小写不敏感）。 */
    @SerializedName("category") val category: String? = null
)
