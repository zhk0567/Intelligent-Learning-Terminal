package com.intangibleheritage.music.feature.stories

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.intangibleheritage.music.core.data.AppRepositories
import com.intangibleheritage.music.core.data.model.StoryFeedItem
import com.intangibleheritage.music.core.resources.R
import com.intangibleheritage.music.core.ui.navigation.InvalidDeepLinkScreen
import com.intangibleheritage.music.core.ui.theme.ScreenLayout
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryDetailScreen(
    storyId: String,
    onBack: () -> Unit,
    onOpenProduct: (String) -> Unit
) {
    val story = AppRepositories.stories.storyById(storyId)
    if (story == null) {
        InvalidDeepLinkScreen(
            title = stringResource(R.string.story_detail_title),
            message = stringResource(R.string.story_detail_not_found),
            onBack = onBack
        )
        return
    }

    val scope = rememberCoroutineScope()
    val favIds by AppRepositories.profile.favoriteIds().collectAsStateWithLifecycle(emptySet())
    val snackbarHostState = remember { SnackbarHostState() }
    var comment by remember { mutableStateOf("") }
    val likeMsg = stringResource(R.string.story_detail_interact_like)
    val shareMsg = stringResource(R.string.story_detail_interact_share)
    val commentSentMsg = stringResource(R.string.story_detail_comment_sent)
    val commentEmptyMsg = stringResource(R.string.story_detail_comment_empty_hint)
    LaunchedEffect(storyId) {
        AppRepositories.profile.addHistory(storyId)
    }

    val isFavorite = storyId in favIds

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.story_detail_title),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                AppRepositories.profile.toggleFavorite(storyId)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = stringResource(R.string.story_favorite_cd),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        StoryDetailBody(
            story = story,
            comment = comment,
            onCommentChange = { comment = it },
            onSendComment = {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        if (comment.isBlank()) commentEmptyMsg else {
                            comment = ""
                            commentSentMsg
                        }
                    )
                }
            },
            onLike = {
                scope.launch { snackbarHostState.showSnackbar(likeMsg) }
            },
            onShare = {
                scope.launch { snackbarHostState.showSnackbar(shareMsg) }
            },
            onOpenProduct = { onOpenProduct(storyToProductId(storyId)) },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}

@Composable
private fun StoryDetailBody(
    story: StoryFeedItem,
    comment: String,
    onCommentChange: (String) -> Unit,
    onSendComment: () -> Unit,
    onLike: () -> Unit,
    onShare: () -> Unit,
    onOpenProduct: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = ScreenLayout.HorizontalPadding)
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = ScreenLayout.BottomSpacing)
    ) {
        item { Spacer(modifier = Modifier.height(ScreenLayout.TopSpacing)) }
        item {
            AsyncImage(
                model = story.imageRes,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
        story.overlayTextRes?.let { overlayRes ->
            item { Spacer(modifier = Modifier.height(ScreenLayout.GroupSpacing)) }
            item {
                Text(
                    text = stringResource(overlayRes),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
            ) {
                Text(
                    text = stringResource(R.string.story_detail_body),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(ScreenLayout.CardContentPadding)
                )
            }
        }
        item { Spacer(modifier = Modifier.height(ScreenLayout.GroupSpacing)) }
        item {
            Text(
                text = stringResource(R.string.section_actions),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        item { Spacer(modifier = Modifier.height(ScreenLayout.TopSpacing)) }
        item {
            Row {
                FilledTonalButton(onClick = onLike) {
                    Icon(Icons.Filled.ThumbUp, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.story_detail_interact_like))
                }
                Spacer(modifier = Modifier.width(10.dp))
                FilledTonalButton(onClick = onShare) {
                    Icon(Icons.Outlined.IosShare, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.story_detail_interact_share))
                }
            }
        }
        item { Spacer(modifier = Modifier.height(ScreenLayout.SectionSpacing)) }
        item {
            Text(
                text = stringResource(R.string.story_detail_related),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        item { Spacer(modifier = Modifier.height(ScreenLayout.TopSpacing)) }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
            ) {
                Text(
                    text = stringResource(R.string.story_detail_related_hint),
                    modifier = Modifier.padding(ScreenLayout.CardContentPadding),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FilledTonalButton(
                    onClick = onOpenProduct,
                    modifier = Modifier
                        .padding(horizontal = ScreenLayout.CardContentPadding)
                        .padding(bottom = ScreenLayout.CardContentPadding)
                ) {
                    Text(stringResource(R.string.story_detail_open_related_product))
                }
            }
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }
        item {
            Text(
                text = stringResource(R.string.community_comments_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        item { Spacer(modifier = Modifier.height(ScreenLayout.TopSpacing)) }
        item {
            OutlinedTextField(
                value = comment,
                onValueChange = { onCommentChange(it.take(120)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.story_detail_comment_hint)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
        }
        item { Spacer(modifier = Modifier.height(ScreenLayout.TopSpacing)) }
        item {
            FilledTonalButton(
                onClick = onSendComment,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.story_detail_comment_send))
            }
        }
    }
}

private fun storyToProductId(storyId: String): String = when (storyId) {
    "s1" -> "dunhuang_magnet"
    "s2" -> "silk_scarf"
    "s3" -> "bronze_bells"
    else -> "pipa_bookmark"
}
