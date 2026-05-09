package com.guyunxinchuan.heritage.music

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper

/**
 * 乐库 / 全屏播放器共用的网络音频（与 Web `public/audio/...` 一致）。
 * 前五首（国风轮播）无 `audioSrc`，不经过本类。
 */
object PlayerAudioEngine {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var player: MediaPlayer? = null
    private var boundUrl: String? = null
    private var boundTrackIndex: Int = -1
    private var onPlaybackComplete: (() -> Unit)? = null

    fun setOnPlaybackComplete(listener: (() -> Unit)?) {
        onPlaybackComplete = listener
    }

    fun isSamePreparedTrack(trackIndex: Int, url: String?): Boolean =
        trackIndex == boundTrackIndex && url != null && url == boundUrl && player != null

    /** 当前 [MediaPlayer] 是否对应 [PlayerSyncState.currentTrackIndex]（用于进度同步）。 */
    fun drivesSyncState(): Boolean =
        player != null && boundTrackIndex == PlayerSyncState.currentTrackIndex

    fun isPlaying(): Boolean = player?.isPlaying == true

    fun currentPositionMs(): Int = runCatching { player?.currentPosition ?: 0 }.getOrDefault(0)

    fun pause() {
        runCatching { player?.pause() }
    }

    fun start() {
        runCatching { player?.start() }
    }

    fun seekTo(ms: Int) {
        runCatching { player?.seekTo(ms.coerceAtLeast(0)) }
    }

    fun stop() {
        runCatching {
            player?.setOnCompletionListener(null)
            player?.setOnErrorListener(null)
            player?.setOnPreparedListener(null)
            player?.stop()
            player?.release()
        }
        player = null
        boundUrl = null
        boundTrackIndex = -1
    }

    /**
     * 播放或恢复 [url]；若与 [boundTrackIndex] 且 URL 相同且 [MediaPlayer] 仍在，则 [seekTo] 后 [start]。
     * 均在主线程回调。
     */
    fun playUrl(
        appContext: Context,
        trackIndex: Int,
        url: String,
        seekStartMs: Int,
        onPrepared: () -> Unit,
        onError: (String) -> Unit,
    ) {
        if (isSamePreparedTrack(trackIndex, url) && player != null) {
            val dur = (player?.duration?.takeIf { it > 0 } ?: PlayerSyncState.trackDurationMs).coerceAtLeast(1)
            seekTo(seekStartMs.coerceIn(0, dur - 1))
            start()
            onPrepared()
            return
        }
        stop()
        boundUrl = url
        boundTrackIndex = trackIndex
        try {
            val p = MediaPlayer()
            p.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build(),
            )
            p.setDataSource(appContext.applicationContext, android.net.Uri.parse(url))
            p.setOnPreparedListener { mp ->
                val dur = mp.duration.takeIf { it > 0 } ?: PlayerSyncState.trackDurationMs
                if (dur > 0) {
                    PlayerSyncState.trackDurationMs = dur
                }
                val d = PlayerSyncState.trackDurationMs.coerceAtLeast(1)
                mp.seekTo(seekStartMs.coerceIn(0, (d - 1).coerceAtLeast(0)))
                mp.start()
                onPrepared()
            }
            p.setOnCompletionListener {
                mainHandler.post {
                    stop()
                    onPlaybackComplete?.invoke()
                }
            }
            p.setOnErrorListener { _, what, extra ->
                mainHandler.post {
                    val msg = "音频播放失败 what=$what extra=$extra"
                    stop()
                    onError(msg)
                }
                true
            }
            p.prepareAsync()
            player = p
        } catch (e: Exception) {
            stop()
            onError(e.message ?: e.toString())
        }
    }
}
