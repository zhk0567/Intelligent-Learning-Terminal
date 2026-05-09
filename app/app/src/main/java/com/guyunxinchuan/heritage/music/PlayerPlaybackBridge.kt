package com.guyunxinchuan.heritage.music

import android.content.Context

/**
 * 乐库迷你条与 [PlayerActivity] 共用的播放/暂停（网络音频 + 无音频时的演示计时）。
 */
object PlayerPlaybackBridge {

    /**
     * @param onStreamPrepared [MediaPlayer] 已开始播放后回调（主线程），用于启动进度刷新。
     */
    fun togglePlayPause(context: Context, onStreamPrepared: (() -> Unit)? = null) {
        val wantPlay = !PlayerSyncState.isPlaying
        if (!wantPlay) {
            if (PlayerAudioEngine.drivesSyncState()) {
                PlayerSyncState.currentPositionMs =
                    PlayerAudioEngine.currentPositionMs().coerceAtMost(PlayerSyncState.trackDurationMs)
            }
            PlayerAudioEngine.pause()
            PlayerSyncState.updatePlayingState(false)
            return
        }
        val track = PlayerSyncState.currentTrack()
        val url = track.audioRemoteUrl
        if (url.isNullOrBlank()) {
            PlayerSyncState.updatePlayingState(true)
            return
        }
        // 先进入播放态，避免 MediaPlayer.prepareAsync 期间主按钮长时间停在「播放」图标。
        PlayerSyncState.updatePlayingState(true)
        val idx = PlayerSyncState.currentTrackIndex
        val startMs = PlayerSyncState.currentPositionMs
        PlayerAudioEngine.playUrl(
            context,
            idx,
            url,
            startMs,
            onPrepared = {
                PlayerSyncState.updatePlayingState(true)
                onStreamPrepared?.invoke()
            },
            onError = { msg ->
                PlayerSyncState.updatePlayingState(false)
                UiFeedback.toast(context, msg)
            },
        )
    }

    /**
     * 切歌后调用：停止旧解码；若之前在播且新歌有 URL 则重新拉流。
     * [onStreamPrepared] 在 MediaPlayer 进入播放态后调用（主线程）。
     */
    fun onTrackChanged(context: Context, wasPlaying: Boolean, onStreamPrepared: (() -> Unit)? = null) {
        PlayerAudioEngine.stop()
        if (!wasPlaying) return
        val track = PlayerSyncState.currentTrack()
        val url = track.audioRemoteUrl
        if (url.isNullOrBlank()) {
            PlayerSyncState.updatePlayingState(true)
            onStreamPrepared?.invoke()
            return
        }
        PlayerAudioEngine.playUrl(
            context,
            PlayerSyncState.currentTrackIndex,
            url,
            0,
            onPrepared = {
                PlayerSyncState.updatePlayingState(true)
                onStreamPrepared?.invoke()
            },
            onError = { msg ->
                PlayerSyncState.updatePlayingState(false)
                UiFeedback.toast(context, msg)
            },
        )
    }
}
