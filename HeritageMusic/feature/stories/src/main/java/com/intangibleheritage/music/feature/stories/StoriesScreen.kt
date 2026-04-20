@file:OptIn(
    androidx.compose.foundation.ExperimentalFoundationApi::class,
    androidx.compose.material.ExperimentalMaterialApi::class
)

package com.intangibleheritage.music.feature.stories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.intangibleheritage.music.core.data.AppRepositories
import com.intangibleheritage.music.core.data.model.StoryFeedItem
import com.intangibleheritage.music.core.data.model.StoryFeedTab
import com.intangibleheritage.music.core.resources.R
import com.intangibleheritage.music.core.ui.theme.PrimaryTeal
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Composable
fun StoriesScreen(
    onOpenStory: (String) -> Unit = {},
    onOpenNotifications: () -> Unit = {}
) {
    var tabIndex by remember { mutableIntStateOf(0) }
    val tab = if (tabIndex == 0) StoryFeedTab.Recommend else StoryFeedTab.Following
    var feed by remember { mutableStateOf<List<StoryFeedItem>>(emptyList()) }
    var exhausted by remember { mutableStateOf(false) }
    val gridState = rememberLazyStaggeredGridState()
    val loadMutex = remember { Mutex() }
    val scope = rememberCoroutineScope()
    val favIds by AppRepositories.profile.favoriteIds().collectAsStateWithLifecycle(emptySet())
    var refreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = {
            scope.launch {
                refreshing = true
                feed = AppRepositories.stories.refreshFeed(tab)
                exhausted = false
                refreshing = false
            }
        }
    )

    LaunchedEffect(tab) {
        feed = AppRepositories.stories.feed(tab)
        exhausted = false
    }

    LaunchedEffect(tab, exhausted, gridState, feed.size) {
        if (exhausted) return@LaunchedEffect
        // 仅当「滑到列表末尾」布尔变化时收集，避免滚动过程中 lastVisible 索引每帧变化导致协程与快照压力
        snapshotFlow {
            val li = gridState.layoutInfo
            val total = li.totalItemsCount
            if (total <= 0) return@snapshotFlow false
            val last = li.visibleItemsInfo.lastOrNull()?.index ?: -1
            last >= total - 1
        }
            .distinctUntilChanged()
            .collect { atEnd ->
                if (!atEnd) return@collect
                loadMutex.withLock {
                    if (exhausted) return@withLock
                    val more = AppRepositories.stories.loadMore(tab, feed)
                    if (more.isEmpty()) {
                        exhausted = true
                    } else {
                        feed = feed + more
                    }
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TabRow(
                selectedTabIndex = tabIndex,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                containerColor = Color.Transparent,
                contentColor = PrimaryTeal,
                indicator = { positions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(positions[tabIndex]),
                        color = PrimaryTeal
                    )
                },
                divider = {}
            ) {
                Tab(
                    selected = tabIndex == 0,
                    onClick = { tabIndex = 0 },
                    text = {
                        Text(
                            stringResource(R.string.tab_recommend),
                            color = if (tabIndex == 0) PrimaryTeal else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
                Tab(
                    selected = tabIndex == 1,
                    onClick = { tabIndex = 1 },
                    text = {
                        Text(
                            stringResource(R.string.tab_following),
                            color = if (tabIndex == 1) PrimaryTeal else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
            IconButton(onClick = onOpenNotifications) {
                Icon(
                    Icons.Outlined.Notifications,
                    contentDescription = stringResource(R.string.notifications_title),
                    tint = PrimaryTeal
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (feed.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.list_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (tab == StoryFeedTab.Following) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.tab_following_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .pullRefresh(pullRefreshState)
            ) {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    state = gridState,
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalItemSpacing = 10.dp,
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(feed, key = { it.id }) { item ->
                        val favKey = item.id.substringBefore("_more_")
                        StoryCard(
                            item = item,
                            isFavorite = favKey in favIds,
                            onOpen = { onOpenStory(item.id) },
                            onToggleFavorite = {
                                scope.launch {
                                    AppRepositories.profile.toggleFavorite(favKey)
                                }
                            }
                        )
                    }
                }
                PullRefreshIndicator(
                    refreshing = refreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    contentColor = PrimaryTeal
                )
            }
        }
    }
}

@Composable
private fun StoryCard(
    item: StoryFeedItem,
    isFavorite: Boolean,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(item.heightDp.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        AsyncImage(
            model = item.imageRes,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onOpen),
            contentScale = ContentScale.Crop
        )
        val overlay = item.overlayTextRes
        if (overlay != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onOpen)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                        )
                    )
            )
            Text(
                text = stringResource(overlay),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
                    .clickable(onClick = onOpen),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = stringResource(R.string.story_favorite_cd),
                tint = PrimaryTeal,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
