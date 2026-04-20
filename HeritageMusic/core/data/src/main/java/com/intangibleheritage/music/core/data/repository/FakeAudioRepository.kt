package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.AudioTrack
import com.intangibleheritage.music.core.resources.R

/**
 * 示例音频使用维基共享资源等可直链的演示地址；正式环境请换自有 CDN 或本地 raw。
 */
class FakeAudioRepository : AudioRepository {

    private val tracks = listOf(
        AudioTrack(
            id = "track_echoes_east",
            titleRes = R.string.pick_echoes_east,
            coverImageRes = R.drawable.music_pick_1,
            streamUrl = "https://upload.wikimedia.org/wikipedia/commons/0/04/Beethoven_Moonlight_1st_movement.ogg"
        ),
        AudioTrack(
            id = "track_wall_rhymes",
            titleRes = R.string.pick_wall_rhymes,
            coverImageRes = R.drawable.music_pick_2,
            streamUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e8/Toccata_and_Fugue_in_D_minor.ogg"
        ),
        AudioTrack(
            id = "track_ladies_music",
            titleRes = R.string.pick_ladies_music,
            coverImageRes = R.drawable.music_pick_3,
            streamUrl = "https://upload.wikimedia.org/wikipedia/commons/6/6e/Jesu%2C_Joy_of_Man%27s_Desiring_%28Piano_arrangement%29.ogg"
        )
    )

    private val byId = tracks.associateBy { it.id }

    override fun trackById(id: String): AudioTrack? = byId[id]

    override fun allTracks(): List<AudioTrack> = tracks
}
