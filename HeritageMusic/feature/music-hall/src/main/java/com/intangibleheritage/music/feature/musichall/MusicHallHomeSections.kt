package com.intangibleheritage.music.feature.musichall

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.intangibleheritage.music.core.data.model.BannerSlide
import com.intangibleheritage.music.core.data.model.DailyPick
import com.intangibleheritage.music.core.data.model.HorizontalCard
import com.intangibleheritage.music.core.data.model.HotTile
import com.intangibleheritage.music.core.data.model.TagChip
import com.intangibleheritage.music.core.resources.R

/** 与 [MusicHallScreen] 中轮播虚拟页倍数一致。 */
internal const val MusicHallBannerVirtualMultiplier = 1_000

@Composable
internal fun MusicHallBannerSection(
    bannersFiltered: List<BannerSlide>,
    onPlayTrack: (String) -> Unit,
    onFeedback: (String) -> Unit
) {
    if (bannersFiltered.isEmpty()) return
    val context = LocalContext.current
    Spacer(modifier = Modifier.height(14.dp))
    key(bannersFiltered.size) {
        val n = bannersFiltered.size
        val loop = n > 1
        val virtualCount = if (loop) n * MusicHallBannerVirtualMultiplier else 1
        val startPage = if (loop) n * (MusicHallBannerVirtualMultiplier / 2) else 0
        val pagerState = rememberPagerState(
            initialPage = startPage,
            pageCount = { virtualCount }
        )
        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 1,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 8.4f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
        ) { page ->
            val slide = bannersFiltered[if (loop) page % n else 0]
            AsyncImage(
                model = slide.imageUrl?.takeIf { it.isNotBlank() } ?: slide.imageRes,
                contentDescription = bannerTitle(slide, context),
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        slide.audioTrackId?.let { onPlayTrack(it) }
                            ?: onFeedback(
                                context.getString(
                                    R.string.music_hall_snackbar_banner,
                                    bannerTitle(slide, context)
                                )
                            )
                    },
                contentScale = ContentScale.Crop
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val logicalPage =
                if (loop) pagerState.settledPage % n else pagerState.settledPage
            repeat(n) { i ->
                val selected = logicalPage == i
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(if (selected) 7.dp else 5.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                        )
                )
            }
        }
    }
}

@Composable
internal fun MusicHallDailyHotSection(
    hotFiltered: List<HotTile>,
    onPlayTrack: (String) -> Unit,
    onFeedback: (String) -> Unit,
    onOpenMore: (String) -> Unit
) {
    if (hotFiltered.isEmpty()) return
    val context = LocalContext.current
    Spacer(modifier = Modifier.height(18.dp))
    SectionHeader(
        titleRes = R.string.daily_hot,
        onMoreClick = {
            onOpenMore(context.getString(R.string.daily_hot))
        }
    )
    Spacer(modifier = Modifier.height(10.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        hotFiltered.take(4).forEach { tile ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(0.95f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.88f))
                    .clickable {
                        tile.audioTrackId?.let { tid -> onPlayTrack(tid) }
                            ?: onFeedback(
                                context.getString(
                                    R.string.music_hall_snackbar_hot_play,
                                    hotTitle(tile, context)
                                )
                            )
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = tile.imageUrl?.takeIf { it.isNotBlank() } ?: tile.imageRes,
                    contentDescription = hotTitle(tile, context),
                    modifier = Modifier
                        .size(30.dp)
                        .scale(0.95f),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = hotTitle(tile, context),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
internal fun MusicHallDailyPicksSection(
    picksFilteredResolved: List<DailyPick>,
    queryNonEmpty: Boolean,
    onPlayTrack: (String) -> Unit,
    onFeedback: (String) -> Unit,
    onOpenMore: (String) -> Unit
) {
    val context = LocalContext.current
    Spacer(modifier = Modifier.height(18.dp))
    SectionHeader(
        titleRes = R.string.daily_picks,
        onMoreClick = {
            onOpenMore(context.getString(R.string.daily_picks))
        }
    )
    Spacer(modifier = Modifier.height(10.dp))
    if (picksFilteredResolved.isEmpty() && queryNonEmpty) {
        Text(
            text = stringResource(R.string.search_no_result),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            picksFilteredResolved.take(3).forEach { pick ->
                DailyPickGridCard(
                    pick = pick,
                    onPlayTrack = onPlayTrack,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun MusicHallGuessTagsSection(
    tagsFiltered: List<TagChip>,
    queryNonEmpty: Boolean,
    onOpenTagResult: (String) -> Unit,
    onOpenMore: (String) -> Unit
) {
    val context = LocalContext.current
    Spacer(modifier = Modifier.height(18.dp))
    SectionHeader(
        titleRes = R.string.guess_you_like,
        onMoreClick = { onOpenMore(context.getString(R.string.guess_you_like)) }
    )
    Spacer(modifier = Modifier.height(10.dp))
    if (tagsFiltered.isEmpty() && queryNonEmpty) {
        Text(
            text = stringResource(R.string.search_no_result),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    } else {
        var selectedIndex by remember(tagsFiltered) { mutableIntStateOf(0) }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tagsFiltered.take(3).forEachIndexed { index, tag ->
                val selected = selectedIndex == index
                AssistChip(
                    onClick = {
                        selectedIndex = index
                        onOpenTagResult(context.getString(tag.labelRes))
                    },
                    label = { Text(stringResource(tag.labelRes)) },
                    shape = RoundedCornerShape(20.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                        labelColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    ),
                    border = AssistChipDefaults.assistChipBorder(
                        enabled = true,
                        borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}

@Composable
internal fun MusicHallBottomCardsSection(
    bottomFiltered: List<HorizontalCard>,
    onFeedback: (String) -> Unit,
    onOpenMore: (String) -> Unit
) {
    if (bottomFiltered.isEmpty()) return
    val context = LocalContext.current
    Spacer(modifier = Modifier.height(14.dp))
    val card = bottomFiltered.first()
    HorizontalHighlightCard(
        card = card,
        onClick = {
            val title = context.resources.getString(card.titleRes)
            onOpenMore(title)
        }
    )
    Spacer(modifier = Modifier.height(6.dp))
}

internal fun bannerTitle(b: BannerSlide, context: Context): String =
    b.titleOverride ?: context.getString(b.titleRes)

internal fun hotTitle(h: HotTile, context: Context): String =
    h.titleOverride ?: context.getString(h.titleRes)

internal fun pickTitle(p: DailyPick, context: Context): String =
    p.titleOverride ?: context.getString(p.titleRes)

@Composable
private fun DailyPickGridCard(
    pick: DailyPick,
    onPlayTrack: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val canPlay = pick.audioTrackId != null
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.86f))
            .aspectRatio(0.82f)
            .then(
                if (canPlay) {
                    Modifier.clickable { pick.audioTrackId?.let(onPlayTrack) }
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(
            model = pick.imageUrl?.takeIf { it.isNotBlank() } ?: pick.imageRes,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.56f)
                .aspectRatio(1f),
            contentScale = ContentScale.Fit
        )
        Text(
            text = pickTitle(pick, LocalContext.current),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun HorizontalHighlightCard(
    card: HorizontalCard,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.36f), RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.88f))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = card.imageRes,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(card.titleRes),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(R.string.pick_wall_rhymes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SectionHeader(
    titleRes: Int,
    onMoreClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.view_more),
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onMoreClick)
                .padding(horizontal = 6.dp, vertical = 2.dp)
                .wrapContentWidth(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
