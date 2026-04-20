package com.intangibleheritage.music.feature.musichall

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.intangibleheritage.music.core.data.AppRepositories
import com.intangibleheritage.music.core.data.model.MusicHallHomeData
import com.intangibleheritage.music.core.resources.R
import com.intangibleheritage.music.core.ui.theme.BorderTeal
import com.intangibleheritage.music.core.ui.theme.PrimaryTeal
import com.intangibleheritage.music.core.ui.theme.SurfaceCard
import kotlinx.coroutines.delay

@Composable
fun MusicHallScreen(
    onPlayTrack: (String) -> Unit = {},
    onFeedback: (String) -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onOpenMore: (String) -> Unit = {},
    onOpenTagResult: (String) -> Unit = {}
) {
    val repo = AppRepositories.musicHall
    var home by remember { mutableStateOf<MusicHallHomeData?>(null) }
    var query by remember { mutableStateOf("") }
    var debouncedQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        home = repo.loadHome()
    }
    LaunchedEffect(query) {
        delay(220)
        debouncedQuery = query
    }

    val context = LocalContext.current
    val q = debouncedQuery.trim()

    val bannersFiltered = remember(home, q) {
        val list = home?.banners.orEmpty()
        if (q.isEmpty()) list
        else list.filter { bannerTitle(it, context).contains(q, ignoreCase = true) }
    }
    val hotFiltered = remember(home, q) {
        val list = home?.dailyHot.orEmpty()
        if (q.isEmpty()) list
        else {
            list.filter { tile ->
                val title = hotTitle(tile, context)
                val desc = tile.contentDescription
                title.contains(q, ignoreCase = true) ||
                    (desc != null && desc.contains(q, ignoreCase = true))
            }
        }
    }
    val picksFilteredResolved = remember(home, q) {
        val picks = home?.dailyPicks.orEmpty()
        picks.filter { pick ->
            q.isEmpty() || pickTitle(pick, context).contains(q, ignoreCase = true)
        }
    }
    val tagsFiltered = remember(home, q) {
        val tags = home?.guessTags.orEmpty()
        tags.filter { t ->
            q.isEmpty() || context.resources.getString(t.labelRes).contains(q, ignoreCase = true)
        }
    }
    val bottomFiltered = remember(home, q) {
        val bottom = home?.bottomCards.orEmpty()
        bottom.filter { c ->
            q.isEmpty() || context.resources.getString(c.titleRes).contains(q, ignoreCase = true)
        }
    }

    val hasAnyMatch = remember(bannersFiltered, hotFiltered, picksFilteredResolved, tagsFiltered, bottomFiltered, q) {
        if (q.isEmpty()) true
        else {
            bannersFiltered.isNotEmpty() || hotFiltered.isNotEmpty() ||
                picksFilteredResolved.isNotEmpty() || tagsFiltered.isNotEmpty() || bottomFiltered.isNotEmpty()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(stringResource(R.string.search_hint)) },
                    leadingIcon = {
                        Icon(Icons.Outlined.Search, contentDescription = null)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BorderTeal,
                        unfocusedBorderColor = BorderTeal.copy(alpha = 0.5f),
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard
                    )
                )
                IconButton(onClick = onOpenNotifications) {
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = stringResource(R.string.notifications_title),
                        tint = PrimaryTeal
                    )
                }
            }
        }
        if (home?.usedRemoteFallback == true) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.music_hall_offline_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        when {
            home == null -> {
                item {
                    Spacer(modifier = Modifier.height(48.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryTeal)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.music_hall_loading),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
            q.isNotEmpty() && !hasAnyMatch -> {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.search_no_result),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
            else -> {
                item {
                    MusicHallBannerSection(
                        bannersFiltered = bannersFiltered,
                        onPlayTrack = onPlayTrack,
                        onFeedback = onFeedback
                    )
                }
                item {
                    MusicHallDailyHotSection(
                        hotFiltered = hotFiltered,
                        onPlayTrack = onPlayTrack,
                        onFeedback = onFeedback,
                        onOpenMore = onOpenMore
                    )
                }
                item {
                    MusicHallDailyPicksSection(
                        picksFilteredResolved = picksFilteredResolved,
                        queryNonEmpty = q.isNotEmpty(),
                        onPlayTrack = onPlayTrack,
                        onFeedback = onFeedback,
                        onOpenMore = onOpenMore
                    )
                }
                item {
                    MusicHallGuessTagsSection(
                        tagsFiltered = tagsFiltered,
                        queryNonEmpty = q.isNotEmpty(),
                        onOpenTagResult = onOpenTagResult,
                        onOpenMore = onOpenMore
                    )
                }
                item {
                    MusicHallBottomCardsSection(
                        bottomFiltered = bottomFiltered,
                        onFeedback = onFeedback,
                        onOpenMore = onOpenMore
                    )
                }
            }
        }
    }
}
