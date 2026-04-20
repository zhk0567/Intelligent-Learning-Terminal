package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.AudioTrack

interface AudioRepository {
    fun trackById(id: String): AudioTrack?
    fun allTracks(): List<AudioTrack>
}
