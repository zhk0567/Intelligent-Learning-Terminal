package com.intangibleheritage.music.core.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.intangibleheritage.music.core.data.model.BannerSlide
import com.intangibleheritage.music.core.data.model.DailyPick
import com.intangibleheritage.music.core.data.model.HorizontalCard
import com.intangibleheritage.music.core.data.model.HotTile
import com.intangibleheritage.music.core.data.model.MusicHallHomeData
import com.intangibleheritage.music.core.data.model.TagChip
import com.intangibleheritage.music.core.resources.R

class FakeMusicHallRepository : MusicHallRepository {

    override suspend fun loadHome(): MusicHallHomeData = withContext(Dispatchers.Default) {
        MusicHallHomeData(
        banners = listOf(
            BannerSlide(
                id = "b1",
                imageRes = R.drawable.music_banner_1,
                titleRes = R.string.banner_b1_title,
                audioTrackId = "track_echoes_east"
            ),
            BannerSlide(
                id = "b2",
                imageRes = R.drawable.music_banner_2,
                titleRes = R.string.banner_b2_title,
                audioTrackId = "track_wall_rhymes"
            ),
            BannerSlide(
                id = "b3",
                imageRes = R.drawable.music_banner_3,
                titleRes = R.string.banner_b3_title,
                audioTrackId = "track_ladies_music"
            )
        ),
        dailyHot = listOf(
            HotTile("h1", R.drawable.music_hot_1, R.string.hot_h1_title, audioTrackId = "track_echoes_east"),
            HotTile("h2", R.drawable.music_hot_2, R.string.hot_h2_title, audioTrackId = "track_wall_rhymes"),
            HotTile("h3", R.drawable.music_hot_3, R.string.hot_h3_title, audioTrackId = "track_ladies_music"),
            HotTile("h4", R.drawable.music_hot_4, R.string.hot_h4_title, audioTrackId = "track_echoes_east")
        ),
        dailyPicks = listOf(
            DailyPick("p1", R.string.pick_echoes_east, R.drawable.music_pick_1, "track_echoes_east"),
            DailyPick("p2", R.string.pick_wall_rhymes, R.drawable.music_pick_2, "track_wall_rhymes"),
            DailyPick("p3", R.string.pick_ladies_music, R.drawable.music_pick_3, "track_ladies_music")
        ),
        guessTags = listOf(
            TagChip("t1", R.string.tag_folk_festival),
            TagChip("t2", R.string.tag_heritage_charm),
            TagChip("t3", R.string.tag_niche_folk)
        ),
        bottomCards = listOf(
            HorizontalCard("x1", R.string.heritage_crossover, R.drawable.music_crossover),
            HorizontalCard("x2", R.string.basic_learning, R.drawable.music_learning)
        ),
        usedRemoteFallback = false
    )
    }
}
