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

    /** 与小程序 / Web `TRACKS` 顺序一致；5–8 每日热门；9–11 每日精选；12–14 `data/音频/猜你喜欢`（`res/raw/daily_guess_*.mp3`）。 */
    val tracks = listOf(
        SyncTrack("高山流水", "古筝演奏", "国风雅集", 248_000, R.drawable.banner1_img),
        SyncTrack("广陵散", "古琴独奏", "国风雅集", 312_000, R.drawable.banner2_img),
        SyncTrack("二泉映月", "二胡演奏", "民乐经典", 270_000, R.drawable.banner3_img),
        SyncTrack("梅花三弄", "笛子独奏", "民乐经典", 226_000, R.drawable.banner1_img),
        SyncTrack("渔舟唱晚", "古筝演奏", "国风雅集", 295_000, R.drawable.banner2_img),
        SyncTrack("哑女告状", "四平调", "每日热门", 240_000, R.drawable.daily_hot_cover_01),
        SyncTrack("抬花轿", "沁阳唢呐", "每日热门", 240_000, R.drawable.daily_hot_cover_02),
        SyncTrack("美美与共", "箜篌艺术", "每日热门", 240_000, R.drawable.daily_hot_cover_03),
        SyncTrack("醉美玉见", "九莲灯", "每日热门", 240_000, R.drawable.daily_hot_cover_04),
        SyncTrack("杨家将", "河南坠子", "每日精选", 69_000, R.drawable.daily_select_cover_01),
        SyncTrack("杨府挑将", "濮阳大弦戏", "每日精选", 147_000, R.drawable.daily_select_cover_02),
        SyncTrack("湖畔枫吟", "古琴", "每日精选", 93_000, R.drawable.daily_select_cover_03),
        SyncTrack("湘妃竹", "箜篌艺术", "猜你喜欢", 217_000, R.drawable.daily_guess_cover_01),
        SyncTrack("火龙阵", "濮阳大弦戏", "猜你喜欢", 175_000, R.drawable.daily_guess_cover_02),
        SyncTrack("神人畅", "古琴", "猜你喜欢", 162_000, R.drawable.daily_guess_cover_03),
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
