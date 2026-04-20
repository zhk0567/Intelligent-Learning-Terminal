package com.intangibleheritage.music.core.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * 占位图命名约定（替换 data 素材时对齐）：
 * - music_banner_* ：音乐馆轮播
 * - music_hot_* ：每日热门小方图
 * - music_pick_* ：每日精选竖卡
 * - story_feed_* ：故事瀑布流
 * - product_* ：商城商品
 * - profile_grid_* ：我的宫格
 */
data class BannerSlide(
    val id: String,
    @DrawableRes val imageRes: Int = 0,
    /** 远程轮播图 URL；非空时由 Coil 加载 */
    val imageUrl: String? = null,
    @StringRes val titleRes: Int,
    /** 接口下发的标题，优先于 [titleRes] */
    val titleOverride: String? = null,
    val audioTrackId: String? = null
)

data class HotTile(
    val id: String,
    @DrawableRes val imageRes: Int,
    @StringRes val titleRes: Int,
    val titleOverride: String? = null,
    val contentDescription: String? = null,
    val audioTrackId: String? = null,
    /** 远程小方图 URL；非空时由 Coil 优先加载 */
    val imageUrl: String? = null
)

/**
 * 音乐馆首页一次加载的快照（[MusicHallRepository.loadHome]）。
 */
data class MusicHallHomeData(
    val banners: List<BannerSlide>,
    val dailyHot: List<HotTile>,
    val dailyPicks: List<DailyPick>,
    val guessTags: List<TagChip>,
    val bottomCards: List<HorizontalCard>,
    /** 远程不可用或解析失败时，已回退为本地假数据 */
    val usedRemoteFallback: Boolean = false
)

data class DailyPick(
    val id: String,
    @StringRes val titleRes: Int,
    @DrawableRes val imageRes: Int,
    /** 对应 [com.intangibleheritage.music.core.data.repository.AudioRepository] 内曲目 id，可空 */
    val audioTrackId: String? = null,
    val titleOverride: String? = null,
    val imageUrl: String? = null
)

data class TagChip(val id: String, @StringRes val labelRes: Int)

data class HorizontalCard(
    val id: String,
    @StringRes val titleRes: Int,
    @DrawableRes val imageRes: Int
)

data class StoryFeedItem(
    val id: String,
    @DrawableRes val imageRes: Int,
    @StringRes val overlayTextRes: Int? = null,
    val heightDp: Int
)

data class CommunityPost(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val subtitleRes: Int,
    val category: CommunityCategory,
    @StringRes val bodyRes: Int,
    /** 非空时 UI 优先展示该文案（远程 DTO），否则用 [titleRes] 等 string 资源。 */
    val titleOverride: String? = null,
    val subtitleOverride: String? = null,
    val bodyOverride: String? = null
)

enum class CommunityCategory { FolkInstrument, Electronic, Ai }

data class Product(
    val id: String,
    @StringRes val titleRes: Int,
    val priceYuan: Int,
    val rating: Float,
    val reviewCount: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val imageRes: Int,
    @StringRes val shortSubtitleRes: Int? = null
)

data class ProfileGridItem(
    val id: String,
    @DrawableRes val imageRes: Int
)
