package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.BannerSlide
import com.intangibleheritage.music.core.data.model.HotTile
import com.intangibleheritage.music.core.data.model.MusicHallHomeData
import com.intangibleheritage.music.core.network.BannerDto
import com.intangibleheritage.music.core.network.HotTileDto
import com.intangibleheritage.music.core.network.MusicHomeResponseDto
import com.intangibleheritage.music.core.resources.R

/**
 * 将 [MusicHomeResponseDto] 与本地假数据合并；某区块接口为空时沿用假数据。
 */
internal object MusicHallDtoMapper {

    fun merge(dto: MusicHomeResponseDto, fake: MusicHallHomeData): MusicHallHomeData {
        val bannerList = dto.banners
            ?.mapNotNull { it.toBannerSlide() }
            ?.takeIf { it.isNotEmpty() }
        val hotList = dto.hotTiles
            ?.mapNotNull { it.toHotTile() }
            ?.takeIf { it.isNotEmpty() }
        val apiSectionEmpty = dto.banners.isNullOrEmpty() && dto.hotTiles.isNullOrEmpty()
        return MusicHallHomeData(
            banners = bannerList ?: fake.banners,
            dailyHot = hotList ?: fake.dailyHot,
            dailyPicks = fake.dailyPicks,
            guessTags = fake.guessTags,
            bottomCards = fake.bottomCards,
            usedRemoteFallback = apiSectionEmpty
        )
    }

    private fun BannerDto.toBannerSlide(): BannerSlide? {
        val sid = id?.trim().orEmpty().ifEmpty { return null }
        val url = imageUrl?.trim().orEmpty()
        val titleRes = titleResForBanner(sid)
        return BannerSlide(
            id = sid,
            imageRes = if (url.isEmpty()) drawableForBanner(sid) else 0,
            imageUrl = url.takeIf { it.isNotEmpty() },
            titleRes = titleRes,
            titleOverride = title?.trim()?.takeIf { it.isNotEmpty() },
            audioTrackId = audioTrackId?.trim()?.takeIf { it.isNotEmpty() }
        )
    }

    private fun HotTileDto.toHotTile(): HotTile? {
        val sid = id?.trim().orEmpty().ifEmpty { return null }
        val url = imageUrl?.trim().orEmpty()
        val titleText = title?.trim().orEmpty()
        return HotTile(
            id = sid,
            imageRes = if (url.isEmpty()) drawableForHot(sid) else 0,
            titleRes = titleResForHot(sid),
            titleOverride = titleText.takeIf { it.isNotEmpty() },
            contentDescription = titleText.takeIf { it.isNotEmpty() },
            audioTrackId = audioTrackId?.trim()?.takeIf { it.isNotEmpty() },
            imageUrl = url.takeIf { it.isNotEmpty() }
        )
    }

    private fun titleResForHot(id: String): Int = when (id) {
        "h1" -> R.string.hot_h1_title
        "h2" -> R.string.hot_h2_title
        "h3" -> R.string.hot_h3_title
        "h4" -> R.string.hot_h4_title
        else -> R.string.app_name
    }

    private fun titleResForBanner(id: String): Int = when (id) {
        "b1" -> R.string.banner_b1_title
        "b2" -> R.string.banner_b2_title
        "b3" -> R.string.banner_b3_title
        else -> R.string.app_name
    }

    private fun drawableForBanner(id: String): Int = when (id) {
        "b1" -> R.drawable.music_banner_1
        "b2" -> R.drawable.music_banner_2
        "b3" -> R.drawable.music_banner_3
        else -> R.drawable.music_banner_1
    }

    private fun drawableForHot(id: String): Int = when (id) {
        "h1" -> R.drawable.music_hot_elem_1
        "h2" -> R.drawable.music_hot_elem_2
        "h3" -> R.drawable.music_hot_elem_3
        "h4" -> R.drawable.music_hot_elem_4
        else -> R.drawable.music_hot_elem_1
    }
}
