package com.intangibleheritage.music.feature.musichall

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.intangibleheritage.music.core.data.AppRepositories
import com.intangibleheritage.music.core.data.model.CompositionResult
import com.intangibleheritage.music.core.data.model.MentorReviewResult
import com.intangibleheritage.music.core.resources.R
import com.intangibleheritage.music.core.ui.navigation.HeritageSecondaryTopBar
import com.intangibleheritage.music.core.ui.theme.ScreenLayout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HeritageInteractiveHubScreen(
    onBack: () -> Unit,
    onOpenComposition: () -> Unit,
    onOpenReview: () -> Unit
) {
    var analyticsTick by remember { mutableIntStateOf(0) }
    val analytics = remember(analyticsTick) { AppRepositories.interactionAnalytics.snapshot() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeritageSecondaryTopBar(
                title = stringResource(R.string.interactive_hub_title),
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
                    text = stringResource(R.string.interactive_hub_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = ScreenLayout.TopSpacing)
                )
            }
            item {
                InteractiveEntryCard(
                    title = stringResource(R.string.interactive_compose_title),
                    desc = stringResource(R.string.interactive_compose_desc),
                    action = stringResource(R.string.interactive_open_compose),
                    onClick = onOpenComposition
                )
            }
            item {
                InteractiveEntryCard(
                    title = stringResource(R.string.interactive_review_title),
                    desc = stringResource(R.string.interactive_review_desc),
                    action = stringResource(R.string.interactive_open_review),
                    onClick = onOpenReview
                )
            }
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.interactive_analytics_title),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = stringResource(
                                R.string.interactive_analytics_compose_generated,
                                analytics.composeGenerateCount
                            ),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = stringResource(
                                R.string.interactive_analytics_compose_listen,
                                analytics.composeListenCount
                            ),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = stringResource(
                                R.string.interactive_analytics_compose_collect,
                                analytics.composeCollectCount
                            ),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = stringResource(
                                R.string.interactive_analytics_review_generated,
                                analytics.reviewGenerateCount
                            ),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.interactive_analytics_recent_events),
                            style = MaterialTheme.typography.labelLarge
                        )
                        analytics.recentEvents.take(8).forEach { event ->
                            Text(
                                text = stringResource(
                                    R.string.interactive_analytics_event_item,
                                    formatEventTime(event.timestampMs),
                                    event.action
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(onClick = { analyticsTick++ }) {
                            Text(stringResource(R.string.interactive_analytics_refresh))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeritageCompositionScreen(
    onBack: () -> Unit
) {
    var style by remember { mutableStateOf("国风融合") }
    var mood by remember { mutableStateOf("沉静") }
    var tempo by remember { mutableIntStateOf(96) }
    var result by remember { mutableStateOf<CompositionResult?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeritageSecondaryTopBar(
                title = stringResource(R.string.interactive_compose_title),
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
                    text = stringResource(R.string.interactive_compose_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = ScreenLayout.TopSpacing)
                )
            }
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = style,
                            onValueChange = { style = it },
                            label = { Text(stringResource(R.string.interactive_style_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = mood,
                            onValueChange = { mood = it },
                            label = { Text(stringResource(R.string.interactive_mood_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = tempo.toString(),
                            onValueChange = { tempo = it.toIntOrNull() ?: tempo },
                            label = { Text(stringResource(R.string.interactive_tempo_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Button(
                            onClick = {
                                result = AppRepositories.composition.compose(style, mood, tempo)
                                AppRepositories.interactionAnalytics.trackComposeGenerated()
                            }
                        ) {
                            Text(stringResource(R.string.interactive_compose_generate))
                        }
                    }
                }
            }
            result?.let { output ->
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "${stringResource(R.string.interactive_result_clip)}：${output.clipName}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = output.clipHint,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = {
                                    AppRepositories.interactionAnalytics.trackComposeListenClicked()
                                }) {
                                    Text(stringResource(R.string.interactive_action_listen))
                                }
                                Button(onClick = {
                                    AppRepositories.interactionAnalytics.trackComposeCollectClicked()
                                }) {
                                    Text(stringResource(R.string.interactive_action_collect))
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
fun HeritageMentorReviewScreen(
    onBack: () -> Unit
) {
    var audioName by remember { mutableStateOf("我的练习片段.wav") }
    var focus by remember { mutableStateOf("节奏稳定性") }
    var result by remember { mutableStateOf<MentorReviewResult?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeritageSecondaryTopBar(
                title = stringResource(R.string.interactive_review_title),
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
                    text = stringResource(R.string.interactive_review_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = ScreenLayout.TopSpacing)
                )
            }
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = audioName,
                            onValueChange = { audioName = it },
                            label = { Text(stringResource(R.string.interactive_review_audio_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = focus,
                            onValueChange = { focus = it },
                            label = { Text(stringResource(R.string.interactive_review_focus_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Button(onClick = {
                            result = AppRepositories.mentorReview.review(audioName, focus)
                            AppRepositories.interactionAnalytics.trackReviewGenerated()
                        }) {
                            Text(stringResource(R.string.interactive_review_generate))
                        }
                    }
                }
            }
            result?.let { output ->
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(output.summary, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${stringResource(R.string.interactive_score_rhythm)}：${output.rhythmScore}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "${stringResource(R.string.interactive_score_pitch)}：${output.intonationScore}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "${stringResource(R.string.interactive_score_expression)}：${output.expressionScore}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.interactive_review_suggestions),
                                style = MaterialTheme.typography.labelLarge
                            )
                            output.suggestions.forEach { item ->
                                Text(
                                    text = "- $item",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InteractiveEntryCard(
    title: String,
    desc: String,
    action: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onClick) {
                Text(action)
            }
        }
    }
}

private fun formatEventTime(timestampMs: Long): String {
    val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return formatter.format(Date(timestampMs))
}
