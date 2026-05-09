package com.guyunxinchuan.heritage.music

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.guyunxinchuan.heritage.music.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val handler = Handler(Looper.getMainLooper())
    private var isShuffleMode = false
    private var isLoopMode = false
    private val playlist get() = PlayerSyncState.tracks

    private val progressUpdater = object : Runnable {
        override fun run() {
            if (!PlayerSyncState.isPlaying) return
            val track = currentTrack()
            val stream = !track.audioRemoteUrl.isNullOrBlank()
            if (stream) {
                PlayerSyncState.syncFromRealtime()
                if (PlayerSyncState.currentPositionMs >= track.durationMs - 250) {
                    return
                }
                renderProgress()
                handler.postDelayed(this, 250L)
                return
            }
            PlayerSyncState.currentPositionMs += 1000
            if (PlayerSyncState.currentPositionMs >= track.durationMs) {
                onTrackComplete()
            } else {
                renderProgress()
                handler.postDelayed(this, 1000L)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val syncedIndex = PlayerSyncState.currentTrackIndex.coerceIn(0, playlist.lastIndex)
        PlayerSyncState.currentTrackIndex = syncedIndex
        PlayerSyncState.currentPositionMs = PlayerSyncState.currentPositionMs
            .coerceIn(0, playlist[syncedIndex].durationMs)

        setupViews()
        renderTrack()
        syncControlVisualState()
        if (PlayerSyncState.isPlaying) {
            handler.removeCallbacks(progressUpdater)
            handler.post(progressUpdater)
        }
    }

    private fun showPlaylistBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val content = layoutInflater.inflate(R.layout.bottom_sheet_player_playlist, null, false)
        val title = content.findViewById<TextView>(R.id.playlistSheetTitle)
        title.text = getString(R.string.player_playlist_sheet_title, playlist.size)
        val rv = content.findViewById<RecyclerView>(R.id.playlistSheetRecycler)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = PlayerPlaylistAdapter(playlist, PlayerSyncState.currentTrackIndex) { index ->
            val wasPlaying = PlayerSyncState.isPlaying
            PlayerSyncState.setTrack(index, playlist[index].durationMs)
            PlayerSyncState.currentPositionMs = 0
            renderTrack()
            PlayerPlaybackBridge.onTrackChanged(this, wasPlaying) {
                handler.removeCallbacks(progressUpdater)
                handler.post(progressUpdater)
            }
            dialog.dismiss()
        }
        dialog.setContentView(content)
        dialog.show()
    }

    private fun setupViews() {
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.playlistButton.setOnClickListenerThrottled {
            showPlaylistBottomSheet()
        }

        binding.playPauseButton.setOnClickListener {
            togglePlayPause()
        }

        binding.previousButton.setOnClickListener {
            playPrevious()
        }

        binding.nextButton.setOnClickListener {
            playNext()
        }

        binding.shufflePlayButton.setOnClickListenerThrottled {
            isShuffleMode = !isShuffleMode
            binding.shufflePlayButton.alpha = if (isShuffleMode) 1f else 0.5f
            UiFeedback.toast(this, if (isShuffleMode) "已开启随机播放" else "已关闭随机播放")
        }

        binding.sequentialPlayButton.setOnClickListenerThrottled {
            isLoopMode = !isLoopMode
            binding.sequentialPlayButton.alpha = if (isLoopMode) 1f else 0.5f
            updateLoopButtonIcon()
            UiFeedback.toast(this, if (isLoopMode) "单曲循环" else "顺序播放")
        }

        binding.favoriteButton.setOnClickListenerThrottled {
            toggleFavorite()
        }

        binding.shareButton.setOnClickListener {
            shareMusic()
        }

        binding.downloadButton.setOnClickListenerThrottled {
            downloadMusic()
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    PlayerSyncState.currentPositionMs = progress
                    renderProgress()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val track = currentTrack()
                if (!track.audioRemoteUrl.isNullOrBlank() && PlayerAudioEngine.drivesSyncState()) {
                    PlayerAudioEngine.seekTo(PlayerSyncState.currentPositionMs)
                }
            }
        })
    }

    private fun currentTrack(): PlayerSyncState.SyncTrack =
        playlist[PlayerSyncState.currentTrackIndex.coerceIn(0, playlist.lastIndex)]

    private fun renderTrack() {
        val track = currentTrack()
        PlayerSyncState.trackDurationMs = track.durationMs
        binding.musicTitleTextView.text = track.title
        binding.artistTextView.text = track.artist
        binding.albumTextView.text = track.album
        binding.coverImageView.loadCoverRemoteOrDrawable(track.coverRemoteUrl, track.coverResId, CoverPreset.Hero)
        binding.seekBar.max = track.durationMs
        binding.totalTimeTextView.text = formatTime(track.durationMs)
        renderProgress()
    }

    private fun renderProgress() {
        binding.seekBar.progress = PlayerSyncState.currentPositionMs
        binding.currentTimeTextView.text = formatTime(PlayerSyncState.currentPositionMs)
    }

    private fun togglePlayPause() {
        PlayerPlaybackBridge.togglePlayPause(this) {
            handler.removeCallbacks(progressUpdater)
            handler.post(progressUpdater)
        }
        updatePlayPauseButton()
        handler.removeCallbacks(progressUpdater)
        if (PlayerSyncState.isPlaying) {
            handler.post(progressUpdater)
        }
    }

    private fun syncControlVisualState() {
        binding.shufflePlayButton.alpha = if (isShuffleMode) 1f else 0.5f
        binding.sequentialPlayButton.alpha = if (isLoopMode) 1f else 0.5f
        updateLoopButtonIcon()
        updatePlayPauseButton()
        refreshFavoriteButtonUi()
    }

    private fun updateLoopButtonIcon() {
        binding.sequentialPlayButton.setImageResource(
            if (isLoopMode) R.drawable.ic_repeat_one else R.drawable.ic_repeat
        )
    }

    private fun refreshFavoriteButtonUi() {
        val favorited = binding.favoriteButton.tag as? Boolean ?: false
        if (favorited) {
            binding.favoriteButton.setImageResource(R.drawable.ic_favorite_filled)
            ImageViewCompat.setImageTintList(binding.favoriteButton, null)
        } else {
            binding.favoriteButton.setImageResource(R.drawable.ic_favorite_border)
            ImageViewCompat.setImageTintList(
                binding.favoriteButton,
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.ancient_gold))
            )
        }
    }

    private fun updatePlayPauseButton() {
        if (PlayerSyncState.isPlaying) {
            binding.playPauseButton.setImageResource(R.drawable.ic_player_main_pause)
        } else {
            binding.playPauseButton.setImageResource(R.drawable.ic_player_main_play)
        }
        binding.playPauseButton.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, R.color.white)
        )
    }

    private fun playPrevious() {
        val wasPlaying = PlayerSyncState.isPlaying
        PlayerSyncState.previousTrack()
        renderTrack()
        PlayerPlaybackBridge.onTrackChanged(this, wasPlaying) {
            handler.removeCallbacks(progressUpdater)
            handler.post(progressUpdater)
        }
    }

    private fun playNext() {
        val wasPlaying = PlayerSyncState.isPlaying
        PlayerSyncState.nextTrack(shuffle = isShuffleMode)
        renderTrack()
        PlayerPlaybackBridge.onTrackChanged(this, wasPlaying) {
            handler.removeCallbacks(progressUpdater)
            handler.post(progressUpdater)
        }
    }

    private fun onTrackComplete() {
        if (!currentTrack().audioRemoteUrl.isNullOrBlank()) {
            return
        }
        if (isLoopMode) {
            PlayerSyncState.currentPositionMs = 0
            renderProgress()
            handler.postDelayed(progressUpdater, 1000)
            return
        }
        playNext()
    }

    private fun toggleFavorite() {
        val isFavorite = binding.favoriteButton.tag as? Boolean ?: false
        binding.favoriteButton.tag = !isFavorite
        refreshFavoriteButtonUi()
        UiFeedback.toast(
            this,
            if (!isFavorite) "已添加到收藏" else "已取消收藏"
        )
    }

    private fun shareMusic() {
        val track = currentTrack()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "分享音乐")
            putExtra(Intent.EXTRA_TEXT, "推荐你听：${track.title} - ${track.artist}")
        }
        startActivity(Intent.createChooser(intent, "分享到"))
    }

    private fun downloadMusic() {
        UiFeedback.toast(this, "开始下载：${currentTrack().title}")
    }

    private fun formatTime(milliseconds: Int): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        PlayerSyncState.trackDurationMs = currentTrack().durationMs
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onPause() {
        PlayerSyncState.trackDurationMs = currentTrack().durationMs
        super.onPause()
    }
}
