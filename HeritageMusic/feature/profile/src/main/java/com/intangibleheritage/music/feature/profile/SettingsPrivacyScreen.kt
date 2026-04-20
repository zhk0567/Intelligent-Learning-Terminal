package com.intangibleheritage.music.feature.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.intangibleheritage.music.core.resources.R
import com.intangibleheritage.music.core.ui.navigation.HeritageSecondaryTopBar
import com.intangibleheritage.music.core.ui.theme.ScreenLayout
import com.intangibleheritage.music.core.ui.theme.SurfaceCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPrivacyScreen(
    onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeritageSecondaryTopBar(
                title = stringResource(R.string.settings_privacy_title),
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
            PrivacyActionRow(
                title = stringResource(R.string.settings_privacy_policy),
                subtitle = stringResource(R.string.settings_privacy_subtitle_policy)
            )
            PrivacyActionRow(
                title = stringResource(R.string.settings_user_agreement),
                subtitle = stringResource(R.string.settings_privacy_subtitle_agreement)
            )
            PrivacyActionRow(
                title = stringResource(R.string.settings_permission_manage),
                subtitle = stringResource(R.string.settings_privacy_subtitle_permission)
            )
            PrivacyActionRow(
                title = stringResource(R.string.settings_data_export),
                subtitle = stringResource(R.string.settings_privacy_subtitle_export)
            )
            PrivacyActionRow(
                title = stringResource(R.string.settings_account_logout),
                subtitle = stringResource(R.string.settings_privacy_subtitle_logout)
            )
        }
    }
}

@Composable
private fun PrivacyActionRow(
    title: String,
    subtitle: String
) {
    ListItem(
        headlineContent = {
            Text(title, color = MaterialTheme.colorScheme.onBackground)
        },
        supportingContent = {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = Modifier.clickable { },
        colors = ListItemDefaults.colors(containerColor = SurfaceCard)
    )
}
