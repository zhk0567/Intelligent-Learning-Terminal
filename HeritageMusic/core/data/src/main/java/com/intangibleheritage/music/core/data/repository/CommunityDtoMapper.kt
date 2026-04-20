package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.CommunityCategory
import com.intangibleheritage.music.core.data.model.CommunityPost
import com.intangibleheritage.music.core.network.CommunityPostDto
import com.intangibleheritage.music.core.network.CommunityPostsResponseDto
import com.intangibleheritage.music.core.resources.R

internal object CommunityDtoMapper {

    fun mapPosts(dto: CommunityPostsResponseDto?): List<CommunityPost>? {
        val raw = dto?.posts ?: return null
        val out = raw.mapNotNull { it.toPost() }
        return out.takeIf { it.isNotEmpty() }
    }

    private fun CommunityPostDto.toPost(): CommunityPost? {
        val sid = id?.trim().orEmpty().ifEmpty { return null }
        val titleText = title?.trim().orEmpty().ifEmpty { return null }
        val bodyText = body?.trim()?.takeIf { it.isNotEmpty() } ?: titleText
        val subText = subtitle?.trim()?.takeIf { it.isNotEmpty() } ?: titleText
        val cat = when (category?.trim()?.lowercase()) {
            "electronic" -> CommunityCategory.Electronic
            "ai" -> CommunityCategory.Ai
            else -> CommunityCategory.FolkInstrument
        }
        return CommunityPost(
            id = sid,
            titleRes = R.string.community_remote_unused,
            subtitleRes = R.string.community_remote_unused,
            bodyRes = R.string.community_remote_unused,
            category = cat,
            titleOverride = titleText,
            subtitleOverride = subText,
            bodyOverride = bodyText
        )
    }
}
