package com.intangibleheritage.music.feature.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import com.intangibleheritage.music.core.data.AppRepositories
import com.intangibleheritage.music.core.data.model.AudioTrack
import com.intangibleheritage.music.core.resources.R
import com.intangibleheritage.music.core.ui.navigation.HeritageSecondaryTopBar
import com.intangibleheritage.music.core.ui.navigation.InvalidDeepLinkScreen
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    trackId: String,
    onBack: () -> Unit,
    onPlayTrackId: (String) -> Unit = {}
) {
    val track = AppRepositories.audio.trackById(trackId)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    if (track == null) {
        InvalidDeepLinkScreen(
            title = stringResource(R.string.player_unknown_track),
            message = stringResource(R.string.nav_invalid_track_message),
            onBack = onBack
        )
        return
    }

    var player by remember { mutableStateOf<ExoPlayer?>(null) }
    var playbackError by remember { mutableStateOf<String?>(null) }
    var isBuffering by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }

    val allTracks = remember { AppRepositories.audio.allTracks() }
    val trackIndex = remember(track.id) { allTracks.indexOfFirst { it.id == track.id } }
    val hasPrev = trackIndex > 0
    val hasNext = trackIndex >= 0 && trackIndex < allTracks.lastIndex

    DisposableEffect(track.id, track.streamUrl) {
        playbackError = null
        val p = ExoPlayer.Builder(context).build()
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                isBuffering = state == Player.STATE_BUFFERING
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlayerError(error: PlaybackException) {
                playbackError =
                    error.message?.takeIf { it.isNotBlank() }
                        ?: context.getString(R.string.player_error_generic)
            }
        }
        p.addListener(listener)
        p.setMediaItem(MediaItem.fromUri(track.streamUrl))
        p.prepare()
        p.playWhenReady = true
        player = p
        scope.launch {
            AppRepositories.profile.addHistory(track.id)
        }
        onDispose {
            p.removeListener(listener)
            p.release()
            player = null
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeritageSecondaryTopBar(
                title = stringResource(track.titleRes),
                onBack = onBack
            )
        }
    ) { padding ->
        PlayerContent(
            track = track,
            player = player,
            playbackError = playbackError,
            isBuffering = isBuffering,
            isPlaying = isPlaying,
            onPlayPause = {
                player?.let { p ->
                    if (p.isPlaying) p.pause() else p.play()
                }
            },
            onRetry = {
                playbackError = null
                player?.let { p ->
                    p.stop()
                    p.setMediaItem(MediaItem.fromUri(track.streamUrl))
                    p.prepare()
                    p.playWhenReady = true
                }
            },
            hasPrev = hasPrev,
            hasNext = hasNext,
            onSkipPrevious = {
                if (hasPrev) onPlayTrackId(allTracks[trackIndex - 1].id)
            },
            onSkipNext = {
                if (hasNext) onPlayTrackId(allTracks[trackIndex + 1].id)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        )
    }
}

@Composable
private fun PlayerContent(
    track: AudioTrack,
    player: ExoPlayer?,
    playbackError: String?,
    isBuffering: Boolean,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onRetry: () -> Unit,
    hasPrev: Boolean,
    hasNext: Boolean,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        PlayerAlbumArt(coverImageRes = track.coverImageRes)

        Spacer(modifier = Modifier.height(16.dp))

        if (playbackError != null) {
            Text(
                text = playbackError,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.player_retry))
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        PlayerPlaybackProgress(
            player = player,
            isBuffering = isBuffering && playbackError == null,
            playbackError = playbackError
        )

        PlayerTransportControls(
            isPlaying = isPlaying,
            hasPrev = hasPrev,
            hasNext = hasNext,
            controlsEnabled = player != null && playbackError == null,
            onPlayPause = onPlayPause,
            onSkipPrevious = onSkipPrevious,
            onSkipNext = onSkipNext
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.player_stream_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PlayerAlbumArt(
    coverImageRes: Int,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = coverImageRes,
        contentDescription = stringResource(R.string.player_cover_cd),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun PlayerPlaybackProgress(
    player: ExoPlayer?,
    isBuffering: Boolean,
    playbackError: String?
) {
    var positionMs by remember(player) { mutableLongStateOf(0L) }
    var durationMs by remember(player) { mutableLongStateOf(0L) }
    var sliderDragging by remember(player) { mutableStateOf(false) }
    var sliderUiFraction by remember(player) { mutableFloatStateOf(0f) }

    val durationForSlider = max(durationMs, 1L)
    val progress = (positionMs.toFloat() / durationForSlider.toFloat()).coerceIn(0f, 1f)
    val sliderValue = if (sliderDragging) sliderUiFraction else progress
    val sliderEnabled = player != null && durationMs > 0 && playbackError == null

    DisposableEffect(player) {
        onDispose {
            positionMs = 0L
            durationMs = 0L
            sliderDragging = false
            sliderUiFraction = 0f
        }
    }
    androidx.compose.runtime.LaunchedEffect(player) {
        if (player == null) return@LaunchedEffect
        while (true) {
            if (!sliderDragging) {
                positionMs = player.currentPosition
                val d = player.duration
                if (d > 0 && d != C.TIME_UNSET) {
                    durationMs = d
                }
            }
            delay(500)
        }
    }

    RowTimeAndBuffering(
        positionMs = positionMs,
        durationMs = durationMs,
        isBuffering = isBuffering,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(4.dp))

    Slider(
        value = sliderValue,
        onValueChange = { v ->
            sliderDragging = true
            sliderUiFraction = v
        },
        onValueChangeFinished = {
            player?.let { p ->
                val dur = p.duration
                if (dur > 0 && dur != C.TIME_UNSET) {
                    p.seekTo((sliderUiFraction * dur).toLong())
                }
            }
            sliderDragging = false
        },
        enabled = sliderEnabled,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun PlayerTransportControls(
    isPlaying: Boolean,
    hasPrev: Boolean,
    hasNext: Boolean,
    controlsEnabled: Boolean,
    onPlayPause: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onSkipPrevious,
            enabled = hasPrev && controlsEnabled
        ) {
            Icon(
                imageVector = Icons.Filled.SkipPrevious,
                contentDescription = stringResource(R.string.player_skip_prev_cd),
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(
            onClick = onPlayPause,
            enabled = controlsEnabled
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) {
                    stringResource(R.string.player_pause_cd)
                } else {
                    stringResource(R.string.player_play_cd)
                },
                modifier = Modifier.size(44.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(
            onClick = onSkipNext,
            enabled = hasNext && controlsEnabled
        ) {
            Icon(
                imageVector = Icons.Filled.SkipNext,
                contentDescription = stringResource(R.string.player_skip_next_cd),
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun RowTimeAndBuffering(
    positionMs: Long,
    durationMs: Long,
    isBuffering: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formatPlayerTime(positionMs),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.weight(1f))
        if (isBuffering) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = stringResource(R.string.player_buffering),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(
                text = formatPlayerTime(durationMs),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatPlayerTime(ms: Long): String {
    if (ms <= 0L) return "0:00"
    val totalSec = ms / 1000L
    val m = totalSec / 60L
    val s = totalSec % 60L
    return String.format(Locale.getDefault(), "%d:%02d", m, s)
}
