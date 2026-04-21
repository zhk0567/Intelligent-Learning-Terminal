package com.intangibleheritage.music.feature.musichall

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.intangibleheritage.music.core.data.AppRepositories
import com.intangibleheritage.music.core.data.model.ArchiveAssetType
import com.intangibleheritage.music.core.data.model.HeritageArchiveAsset
import com.intangibleheritage.music.core.resources.R
import com.intangibleheritage.music.core.ui.navigation.HeritageSecondaryTopBar
import com.intangibleheritage.music.core.ui.navigation.InvalidDeepLinkScreen
import com.intangibleheritage.music.core.ui.theme.ScreenLayout

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HeritageArchiveScreen(
    onBack: () -> Unit,
    onOpenDetail: (String) -> Unit
) {
    val allAssets = remember { AppRepositories.archive.allAssets() }
    var sortMode by remember { mutableStateOf(ArchiveSortMode.CredibilityDesc) }
    var selectedType by remember { mutableStateOf<ArchiveAssetType?>(null) }
    var selectedRegion by remember { mutableStateOf<String?>(null) }
    var selectedGenre by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
    val regions = remember(allAssets) { allAssets.map { it.region }.distinct() }
    val genres = remember(allAssets) { allAssets.map { it.genre }.distinct() }
    val filtered = remember(allAssets, selectedType, selectedRegion, selectedGenre, query, sortMode) {
        val q = query.trim()
        val base = allAssets.filter { item ->
            (selectedType == null || item.type == selectedType) &&
                (selectedRegion == null || item.region == selectedRegion) &&
                (selectedGenre == null || item.genre == selectedGenre) &&
                (
                    q.isEmpty() ||
                        item.title.contains(q, ignoreCase = true) ||
                        item.inheritor.contains(q, ignoreCase = true)
                    )
        }
        when (sortMode) {
            ArchiveSortMode.CredibilityDesc -> base.sortedByDescending { it.credibilityScore }
            ArchiveSortMode.LatestEntry -> base.sortedByDescending { it.sourceTimeline.lastOrNull().orEmpty() }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeritageSecondaryTopBar(
                title = stringResource(R.string.archive_title),
                onBack = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = ScreenLayout.HorizontalPadding),
            contentPadding = PaddingValues(bottom = ScreenLayout.BottomSpacing),
            verticalArrangement = Arrangement.spacedBy(ScreenLayout.GroupSpacing)
        ) {
            item {
                Text(
                    text = stringResource(R.string.archive_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = ScreenLayout.TopSpacing)
                )
                Spacer(modifier = Modifier.height(ScreenLayout.SectionSpacing))
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text(stringResource(R.string.archive_search_hint)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.outline,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ArchiveTypeFilterChip(
                        label = stringResource(R.string.archive_clear_filters),
                        selected = false,
                        onClick = {
                            selectedType = null
                            selectedRegion = null
                            selectedGenre = null
                            query = ""
                        }
                    )
                    ArchiveTypeFilterChip(
                        label = stringResource(R.string.archive_sort_credibility),
                        selected = sortMode == ArchiveSortMode.CredibilityDesc,
                        onClick = { sortMode = ArchiveSortMode.CredibilityDesc }
                    )
                    ArchiveTypeFilterChip(
                        label = stringResource(R.string.archive_sort_latest),
                        selected = sortMode == ArchiveSortMode.LatestEntry,
                        onClick = { sortMode = ArchiveSortMode.LatestEntry }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ArchiveTypeFilterChip(
                        label = stringResource(R.string.archive_filter_all),
                        selected = selectedType == null,
                        onClick = { selectedType = null }
                    )
                    ArchiveAssetType.entries.forEach { type ->
                        ArchiveTypeFilterChip(
                            label = archiveTypeLabel(type),
                            selected = selectedType == type,
                            onClick = { selectedType = type }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ArchiveTypeFilterChip(
                        label = stringResource(R.string.archive_filter_region_all),
                        selected = selectedRegion == null,
                        onClick = { selectedRegion = null }
                    )
                    regions.forEach { region ->
                        ArchiveTypeFilterChip(
                            label = region,
                            selected = selectedRegion == region,
                            onClick = { selectedRegion = region }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ArchiveTypeFilterChip(
                        label = stringResource(R.string.archive_filter_genre_all),
                        selected = selectedGenre == null,
                        onClick = { selectedGenre = null }
                    )
                    genres.forEach { genre ->
                        ArchiveTypeFilterChip(
                            label = genre,
                            selected = selectedGenre == genre,
                            onClick = { selectedGenre = genre }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.archive_result_count, filtered.size),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(filtered, key = { it.id }) { asset ->
                ArchiveListCard(
                    asset = asset,
                    onClick = { onOpenDetail(asset.id) }
                )
            }
        }
    }
}

@Composable
fun HeritageArchiveDetailScreen(
    assetId: String,
    onBack: () -> Unit,
    onOpenStory: (String) -> Unit,
    onOpenTrack: (String) -> Unit
) {
    val asset = remember(assetId) { AppRepositories.archive.assetById(assetId) }
    if (asset == null) {
        InvalidDeepLinkScreen(
            title = stringResource(R.string.nav_invalid_title),
            message = stringResource(R.string.archive_invalid_message),
            onBack = onBack
        )
        return
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeritageSecondaryTopBar(
                title = stringResource(R.string.archive_detail_title),
                onBack = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = ScreenLayout.HorizontalPadding),
            contentPadding = PaddingValues(bottom = ScreenLayout.BottomSpacing),
            verticalArrangement = Arrangement.spacedBy(ScreenLayout.GroupSpacing)
        ) {
            item {
                Spacer(modifier = Modifier.height(ScreenLayout.TopSpacing))
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(ScreenLayout.CardContentPadding)) {
                        AsyncImage(
                            model = asset.coverRes,
                            contentDescription = asset.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = asset.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${stringResource(R.string.archive_detail_type)}：${archiveTypeLabel(asset.type)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${stringResource(R.string.archive_detail_region)}：${asset.region}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${stringResource(R.string.archive_detail_genre)}：${asset.genre}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${stringResource(R.string.archive_detail_era)}：${asset.era}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${stringResource(R.string.archive_detail_inheritor)}：${asset.inheritor}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        ArchiveCopyrightStatusTag(asset.copyrightStatus)
                    }
                }
            }
            item {
                ArchiveInfoSection(
                    title = stringResource(R.string.archive_detail_source_path),
                    body = asset.sourcePath
                )
            }
            item {
                ArchiveInfoSection(
                    title = stringResource(R.string.archive_detail_copyright),
                    body = asset.copyrightStatus
                )
            }
            item {
                ArchiveInfoSection(
                    title = stringResource(R.string.archive_detail_credibility),
                    body = stringResource(R.string.archive_credibility_score, asset.credibilityScore)
                )
            }
            item {
                ArchiveInfoSection(
                    title = stringResource(R.string.archive_detail_timeline),
                    body = asset.sourceTimeline.joinToString(separator = "\n")
                )
            }
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(ScreenLayout.CardContentPadding)) {
                        Text(
                            text = stringResource(R.string.archive_detail_related),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.archive_related_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            val relatedStoryId = asset.relatedStoryId
                            val relatedTrackId = asset.relatedTrackId
                            if (relatedStoryId != null) {
                                Button(onClick = { onOpenStory(relatedStoryId) }) {
                                    Text(stringResource(R.string.archive_open_story))
                                }
                            }
                            if (relatedTrackId != null) {
                                Button(onClick = { onOpenTrack(relatedTrackId) }) {
                                    Text(stringResource(R.string.archive_open_track))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArchiveTypeFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = if (selected) 0.9f else 0.5f))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun ArchiveListCard(
    asset: HeritageArchiveAsset,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Row(modifier = Modifier.padding(ScreenLayout.CardContentPadding)) {
            AsyncImage(
                model = asset.coverRes,
                contentDescription = asset.title,
                modifier = Modifier
                    .height(82.dp)
                    .fillMaxWidth(0.34f),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = asset.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${asset.region} · ${asset.genre}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${stringResource(R.string.archive_detail_inheritor)}：${asset.inheritor}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${stringResource(R.string.archive_detail_copyright)}：${asset.copyrightStatus}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ArchiveCopyrightStatusTag(asset.copyrightStatus)
            }
        }
    }
}

@Composable
private fun ArchiveInfoSection(
    title: String,
    body: String
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(ScreenLayout.CardContentPadding)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ArchiveCopyrightStatusTag(status: String) {
    val (bgColor, labelColor) = when {
        status.contains("签署", ignoreCase = true) || status.contains("签约", ignoreCase = true) ->
            Color(0x2232CD7A) to Color(0xFF32CD7A)
        status.contains("授权", ignoreCase = true) ->
            Color(0x222AA8FF) to Color(0xFF2AA8FF)
        else -> Color(0x22FFB74D) to Color(0xFFFFB74D)
    }
    Card(
        shape = RoundedCornerShape(999.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall,
            color = labelColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private enum class ArchiveSortMode {
    CredibilityDesc,
    LatestEntry
}

@Composable
private fun archiveTypeLabel(type: ArchiveAssetType): String = when (type) {
    ArchiveAssetType.Video -> stringResource(R.string.archive_type_video)
    ArchiveAssetType.Audio -> stringResource(R.string.archive_type_audio)
    ArchiveAssetType.Text -> stringResource(R.string.archive_type_text)
    ArchiveAssetType.Inheritor -> stringResource(R.string.archive_type_inheritor)
}
