package com.guyunxinchuan.heritage.music

import android.os.SystemClock

object PlayerSyncState {
    data class SyncTrack(
        val title: String,
        val artist: String,
        val album: String,
        val durationMs: Int,
        val coverResId: Int
    )

    val tracks = listOf(
        SyncTrack("高山流水", "古筝演奏", "国乐经典", 262000, R.drawable.banner1_img),
        SyncTrack("丝路琴音", "丝路学者", "丝路文化", 238000, R.drawable.banner2_img),
        SyncTrack("编钟回响", "礼乐研究者", "礼乐文化", 251000, R.drawable.banner3_img)
    )

    var isPlaying: Boolean = false
    var currentTrackIndex: Int = 0
    var currentPositionMs: Int = 0
    var trackDurationMs: Int = tracks.first().durationMs
    private var lastTickElapsedMs: Long = SystemClock.elapsedRealtime()

    fun syncFromRealtime() {
        if (!isPlaying) return
        val now = SystemClock.elapsedRealtime()
        val delta = (now - lastTickElapsedMs).toInt().coerceAtLeast(0)
        lastTickElapsedMs = now
        currentPositionMs = (currentPositionMs + delta).coerceAtMost(trackDurationMs)
        if (currentPositionMs >= trackDurationMs) {
            isPlaying = false
        }
    }

    fun updatePlayingState(playing: Boolean) {
        isPlaying = playing
        lastTickElapsedMs = SystemClock.elapsedRealtime()
    }

    fun setTrack(index: Int, duration: Int) {
        currentTrackIndex = index
        trackDurationMs = duration
        currentPositionMs = 0
        lastTickElapsedMs = SystemClock.elapsedRealtime()
    }

    fun currentTrack(): SyncTrack {
        val safeIndex = currentTrackIndex.coerceIn(0, tracks.lastIndex)
        return tracks[safeIndex]
    }

    fun previousTrack() {
        val nextIndex = if (currentTrackIndex == 0) tracks.lastIndex else currentTrackIndex - 1
        setTrack(nextIndex, tracks[nextIndex].durationMs)
    }

    fun nextTrack(shuffle: Boolean) {
        val nextIndex = if (shuffle) {
            tracks.indices.random()
        } else {
            (currentTrackIndex + 1) % tracks.size
        }
        setTrack(nextIndex, tracks[nextIndex].durationMs)
    }
}
