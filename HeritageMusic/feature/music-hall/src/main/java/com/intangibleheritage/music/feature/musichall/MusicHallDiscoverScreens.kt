package com.intangibleheritage.music.feature.musichall

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.intangibleheritage.music.core.resources.R
import com.intangibleheritage.music.core.ui.navigation.HeritageSecondaryTopBar
import com.intangibleheritage.music.core.ui.theme.BorderTeal
import com.intangibleheritage.music.core.ui.theme.ScreenLayout
import com.intangibleheritage.music.core.ui.theme.SurfaceCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicHallMoreScreen(
    sectionTitle: String,
    onBack: () -> Unit
) {
    val items = rememberDiscoverItems(sectionTitle)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeritageSecondaryTopBar(
                title = sectionTitle,
                onBack = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = ScreenLayout.HorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(ScreenLayout.GroupSpacing)
        ) {
            items(items, key = { it }) { title ->
                DiscoverListItem(
                    title = title,
                    subtitle = stringResource(R.string.music_hall_more_hint)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicHallTagResultScreen(
    tagName: String,
    onBack: () -> Unit
) {
    val items = rememberDiscoverItems(tagName)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeritageSecondaryTopBar(
                title = stringResource(R.string.music_hall_tag_result_title, tagName),
                onBack = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = ScreenLayout.HorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(ScreenLayout.GroupSpacing)
        ) {
            items(items, key = { it }) { title ->
                DiscoverListItem(
                    title = title,
                    subtitle = stringResource(R.string.music_hall_tag_result_hint, tagName)
                )
            }
        }
    }
}

@Composable
private fun DiscoverListItem(
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderTeal.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(ScreenLayout.CardContentPadding)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Composable
private fun rememberDiscoverItems(seedTitle: String): List<String> = listOf(
    "$seedTitle · ${stringResource(R.string.music_hall_entry_1)}",
    "$seedTitle · ${stringResource(R.string.music_hall_entry_2)}",
    "$seedTitle · ${stringResource(R.string.music_hall_entry_3)}",
    "$seedTitle · ${stringResource(R.string.music_hall_entry_4)}"
)
