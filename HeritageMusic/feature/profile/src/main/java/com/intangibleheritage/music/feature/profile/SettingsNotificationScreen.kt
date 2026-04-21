package com.intangibleheritage.music.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.intangibleheritage.music.core.resources.R
import com.intangibleheritage.music.core.ui.navigation.HeritageSecondaryTopBar
import com.intangibleheritage.music.core.ui.theme.ScreenLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsNotificationScreen(
    onBack: () -> Unit
) {
    var pushEnabled by remember { mutableStateOf(true) }
    var systemEnabled by remember { mutableStateOf(true) }
    var activityEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrateEnabled by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeritageSecondaryTopBar(
                title = stringResource(R.string.settings_notifications_title),
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = ScreenLayout.HorizontalPadding)
                .verticalScroll(rememberScrollState())
                .padding(vertical = ScreenLayout.TopSpacing),
            verticalArrangement = Arrangement.spacedBy(ScreenLayout.GroupSpacing)
        ) {
            SettingSwitchRow(
                title = stringResource(R.string.settings_notify_push),
                subtitle = stringResource(R.string.settings_notify_subtitle_push),
                checked = pushEnabled,
                onCheckedChange = { pushEnabled = it }
            )
            SettingSwitchRow(
                title = stringResource(R.string.settings_notify_system),
                subtitle = stringResource(R.string.settings_notify_subtitle_system),
                checked = systemEnabled,
                onCheckedChange = { systemEnabled = it }
            )
            SettingSwitchRow(
                title = stringResource(R.string.settings_notify_activity),
                subtitle = stringResource(R.string.settings_notify_subtitle_activity),
                checked = activityEnabled,
                onCheckedChange = { activityEnabled = it }
            )
            SettingSwitchRow(
                title = stringResource(R.string.settings_notify_sound),
                subtitle = stringResource(R.string.settings_notify_subtitle_sound),
                checked = soundEnabled,
                onCheckedChange = { soundEnabled = it }
            )
            SettingSwitchRow(
                title = stringResource(R.string.settings_notify_vibrate),
                subtitle = stringResource(R.string.settings_notify_subtitle_vibrate),
                checked = vibrateEnabled,
                onCheckedChange = { vibrateEnabled = it }
            )
            Text(
                text = stringResource(R.string.settings_demo_saved),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
        }
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    androidx.compose.material3.ListItem(
        headlineContent = {
            Text(text = title, color = MaterialTheme.colorScheme.onBackground)
        },
        supportingContent = {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    )
}
