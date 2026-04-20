package com.intangibleheritage.music.core.data.profile

import com.intangibleheritage.music.core.resources.R

/** 将收藏/历史中的稳定 id 映射到缩略图 drawable（与 data 素材替换时一并调整）。 */
object ProfileVisuals {
    fun thumbnailForContentId(id: String): Int = when (id) {
        "s1" -> R.drawable.story_feed_01
        "s2" -> R.drawable.story_feed_02
        "s3" -> R.drawable.story_feed_03
        "s4" -> R.drawable.story_feed_04
        "s5" -> R.drawable.story_feed_05
        "s6" -> R.drawable.story_feed_06
        "s7" -> R.drawable.story_feed_07
        "s8" -> R.drawable.story_feed_08
        "f1" -> R.drawable.story_feed_03
        "f2" -> R.drawable.story_feed_05
        "f3" -> R.drawable.story_feed_07
        "g1" -> R.drawable.profile_grid_01
        "g2" -> R.drawable.profile_grid_02
        "g3" -> R.drawable.profile_grid_03
        "g4" -> R.drawable.profile_grid_04
        "g5" -> R.drawable.profile_grid_05
        "g6" -> R.drawable.profile_grid_06
        "track_echoes_east" -> R.drawable.music_pick_1
        "track_wall_rhymes" -> R.drawable.music_pick_2
        "track_ladies_music" -> R.drawable.music_pick_3
        "dunhuang_magnet" -> R.drawable.product_dunhuang
        "bronze_bells", "silk_scarf", "pipa_bookmark" -> R.drawable.placeholder_card
        else -> R.drawable.placeholder_card
    }
}
