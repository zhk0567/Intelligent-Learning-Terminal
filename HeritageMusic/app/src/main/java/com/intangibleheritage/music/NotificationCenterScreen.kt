package com.intangibleheritage.music

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.intangibleheritage.music.core.resources.R
import com.intangibleheritage.music.core.ui.navigation.HeritageSecondaryTopBar
import com.intangibleheritage.music.core.ui.theme.PrimaryTeal
import com.intangibleheritage.music.core.ui.theme.SurfaceCard

private data class NotificationItem(
    val id: String,
    val titleRes: Int,
    val bodyRes: Int,
    val timeRes: Int,
    val unread: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCenterScreen(onBack: () -> Unit) {
    var items by remember {
        mutableStateOf(
            listOf(
                NotificationItem("n1", R.string.notifications_item_system_title, R.string.notifications_item_system_body, R.string.notifications_item_time_just_now, true),
                NotificationItem("n2", R.string.notifications_item_story_title, R.string.notifications_item_story_body, R.string.notifications_item_time_today, true),
                NotificationItem("n3", R.string.notifications_item_event_title, R.string.notifications_item_event_body, R.string.notifications_item_time_yesterday, false)
            )
        )
    }

    val unreadCount = items.count { it.unread }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeritageSecondaryTopBar(
                title = stringResource(R.string.notifications_title),
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.notifications_unread_count, unreadCount),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.notifications_mark_all_read),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                    color = PrimaryTeal,
                    modifier = Modifier.clickable {
                        items = items.map { it.copy(unread = false) }
                    }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (items.isEmpty()) {
                Text(
                    text = stringResource(R.string.notifications_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        NotificationCard(
                            item = item,
                            onClick = {
                                if (item.unread) {
                                    items = items.map { if (it.id == item.id) it.copy(unread = false) else it }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    item: NotificationItem,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = SurfaceCard.copy(alpha = if (item.unread) 0.94f else 0.86f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(item.titleRes),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = if (item.unread) FontWeight.SemiBold else FontWeight.Normal
            )
            if (item.unread) {
                Text(
                    text = "  •",
                    style = MaterialTheme.typography.titleSmall,
                    color = PrimaryTeal
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(item.timeRes),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(item.bodyRes),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
