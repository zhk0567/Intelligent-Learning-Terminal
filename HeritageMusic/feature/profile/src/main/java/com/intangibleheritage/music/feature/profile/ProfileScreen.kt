package com.intangibleheritage.music.feature.profile

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.intangibleheritage.music.core.data.AppRepositories
import com.intangibleheritage.music.core.data.model.ProfileGridItem
import com.intangibleheritage.music.core.data.model.UserProfilePrefs
import com.intangibleheritage.music.core.resources.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onGridItemClick: (String) -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onExploreMusicHall: () -> Unit = {},
    onExploreStories: () -> Unit = {}
) {
    var tab by remember { mutableIntStateOf(0) }
    val favorites by AppRepositories.profile.favorites().collectAsStateWithLifecycle(emptyList())
    val history by AppRepositories.profile.watchHistory().collectAsStateWithLifecycle(emptyList())
    val userPrefs by AppRepositories.profile.userProfilePrefs().collectAsStateWithLifecycle(UserProfilePrefs.Default)
    var showEditDialog by remember { mutableStateOf(false) }
    var draftNickname by remember { mutableStateOf("") }
    var draftAvatarKey by remember { mutableStateOf(UserProfilePrefs.AvatarDefault) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(showEditDialog) {
        if (showEditDialog) {
            draftNickname = userPrefs.nickname
            draftAvatarKey = userPrefs.avatarKey
        }
    }

    val grid = if (tab == 0) favorites else history

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showEditDialog = true }
                    .padding(vertical = 4.dp)
            ) {
                AsyncImage(
                    model = avatarDrawableRes(userPrefs.avatarKey),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userPrefs.nickname.ifBlank {
                            stringResource(R.string.username_placeholder)
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = stringResource(R.string.profile_tap_to_edit),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            IconButton(onClick = onOpenSettings) {
                Icon(
                    Icons.Outlined.Settings,
                    contentDescription = stringResource(R.string.settings_cd),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TabRow(
            selectedTabIndex = tab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { positions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(positions[tab]),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            divider = {}
        ) {
            Tab(
                selected = tab == 0,
                onClick = { tab = 0 },
                text = {
                    Text(
                        stringResource(R.string.tab_favorites),
                        color = if (tab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
            Tab(
                selected = tab == 1,
                onClick = { tab = 1 },
                text = {
                    Text(
                        stringResource(R.string.tab_history),
                        color = if (tab == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (grid.isEmpty()) {
            ProfileEmptyPanel(
                isFavoritesTab = tab == 0,
                onExploreMusicHall = onExploreMusicHall,
                onExploreStories = onExploreStories,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (tab == 1 && history.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                scope.launch {
                                    AppRepositories.profile.clearWatchHistory()
                                }
                            }
                        ) {
                            Text(stringResource(R.string.profile_clear_all_history))
                        }
                    }
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(grid, key = { it.id }) { item ->
                        ProfileGridCell(
                            item = item,
                            onClick = { onGridItemClick(item.id) },
                            showDeleteHistory = tab == 1,
                            onDeleteHistory = {
                                scope.launch {
                                    AppRepositories.profile.removeHistoryItem(item.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(stringResource(R.string.profile_edit_title)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = draftNickname,
                        onValueChange = { v -> draftNickname = v.take(UserProfileNicknameMax) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.profile_nickname_label)) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.outline,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.profile_avatar_label),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AvatarOption(
                            res = R.drawable.placeholder_card,
                            selected = draftAvatarKey == UserProfilePrefs.AvatarDefault,
                            onClick = { draftAvatarKey = UserProfilePrefs.AvatarDefault }
                        )
                        AvatarOption(
                            res = R.drawable.music_pick_1,
                            selected = draftAvatarKey == UserProfilePrefs.AvatarPick1,
                            onClick = { draftAvatarKey = UserProfilePrefs.AvatarPick1 }
                        )
                        AvatarOption(
                            res = R.drawable.music_pick_2,
                            selected = draftAvatarKey == UserProfilePrefs.AvatarPick2,
                            onClick = { draftAvatarKey = UserProfilePrefs.AvatarPick2 }
                        )
                        AvatarOption(
                            res = R.drawable.music_pick_3,
                            selected = draftAvatarKey == UserProfilePrefs.AvatarPick3,
                            onClick = { draftAvatarKey = UserProfilePrefs.AvatarPick3 }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            AppRepositories.profile.setUserNickname(draftNickname)
                            AppRepositories.profile.setUserAvatarKey(draftAvatarKey)
                        }
                        showEditDialog = false
                    }
                ) {
                    Text(stringResource(R.string.profile_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text(stringResource(R.string.profile_cancel))
                }
            }
        )
    }
}

private const val UserProfileNicknameMax = 24

@Composable
private fun AvatarOption(
    res: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val border =
        if (selected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape) else Modifier
    AsyncImage(
        model = res,
        contentDescription = null,
        modifier = Modifier
            .size(48.dp)
            .then(border)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun ProfileEmptyPanel(
    isFavoritesTab: Boolean,
    onExploreMusicHall: () -> Unit,
    onExploreStories: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isFavoritesTab) Icons.Outlined.FavoriteBorder else Icons.Outlined.History,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(
                if (isFavoritesTab) R.string.profile_empty_favorites_title else R.string.profile_empty_history_title
            ),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(
                if (isFavoritesTab) R.string.profile_empty_favorites_hint else R.string.profile_empty_history_hint
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(20.dp))
        FilledTonalButton(onClick = onExploreStories) {
            Text(stringResource(R.string.profile_explore_stories))
        }
        Spacer(modifier = Modifier.height(10.dp))
        FilledTonalButton(onClick = onExploreMusicHall) {
            Text(stringResource(R.string.profile_explore_music_hall))
        }
    }
}

@Composable
private fun ProfileGridCell(
    item: ProfileGridItem,
    onClick: () -> Unit,
    showDeleteHistory: Boolean = false,
    onDeleteHistory: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.medium)
    ) {
        AsyncImage(
            model = item.imageRes,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick),
            contentScale = ContentScale.Crop
        )
        if (showDeleteHistory) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.88f))
                    .clickable(onClick = onDeleteHistory),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = stringResource(R.string.profile_remove_history_cd),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun avatarDrawableRes(avatarKey: String): Int = when (avatarKey) {
    UserProfilePrefs.AvatarPick1 -> R.drawable.music_pick_1
    UserProfilePrefs.AvatarPick2 -> R.drawable.music_pick_2
    UserProfilePrefs.AvatarPick3 -> R.drawable.music_pick_3
    else -> R.drawable.placeholder_card
}
