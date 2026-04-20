package com.intangibleheritage.music.feature.community

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.intangibleheritage.music.core.data.AppRepositories
import com.intangibleheritage.music.core.data.model.CommunityCategory
import com.intangibleheritage.music.core.data.model.CommunityPost
import com.intangibleheritage.music.core.resources.R
import com.intangibleheritage.music.core.ui.theme.BorderTeal
import com.intangibleheritage.music.core.ui.theme.PrimaryTeal
import com.intangibleheritage.music.core.ui.theme.SurfaceCard

@Composable
fun CommunityScreen(
    onOpenPost: (String) -> Unit = {},
    onComposeClick: () -> Unit = {}
) {
    var category by remember { mutableStateOf(CommunityCategory.FolkInstrument) }
    var posts by remember { mutableStateOf<List<CommunityPost>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    LaunchedEffect(category) {
        loading = true
        posts = AppRepositories.community.posts(category)
        loading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.community_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CategoryChip(
                    label = stringResource(R.string.filter_folk_instruments),
                    selected = category == CommunityCategory.FolkInstrument,
                    onClick = { category = CommunityCategory.FolkInstrument }
                )
                CategoryChip(
                    label = stringResource(R.string.filter_electronic),
                    selected = category == CommunityCategory.Electronic,
                    onClick = { category = CommunityCategory.Electronic }
                )
                CategoryChip(
                    label = stringResource(R.string.filter_ai),
                    selected = category == CommunityCategory.Ai,
                    onClick = { category = CommunityCategory.Ai }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            when {
                loading -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryTeal)
                    }
                }
                posts.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.list_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        items(posts, key = { it.id }) { post ->
                            CommunityPostCard(
                                post = post,
                                onClick = { onOpenPost(post.id) }
                            )
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = onComposeClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp),
            containerColor = PrimaryTeal,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.compose_fab_cd),
                tint = Color.White
            )
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        shape = RoundedCornerShape(20.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = BorderTeal.copy(alpha = 0.25f),
            containerColor = SurfaceCard,
            labelColor = MaterialTheme.colorScheme.onSurface,
            selectedLabelColor = BorderTeal
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = BorderTeal
        )
    )
}

@Composable
private fun CommunityPostCard(
    post: CommunityPost,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .border(1.dp, BorderTeal, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(
            text = post.titleOverride ?: stringResource(post.titleRes),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = post.subtitleOverride ?: stringResource(post.subtitleRes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
