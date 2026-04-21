package com.intangibleheritage.music.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.intangibleheritage.music.core.data.AppRepositories
import com.intangibleheritage.music.core.data.model.UserProfilePrefs
import com.intangibleheritage.music.core.resources.R
import com.intangibleheritage.music.core.ui.navigation.HeritageSecondaryTopBar
import com.intangibleheritage.music.core.ui.theme.ScreenLayout
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsThemeScreen(
    onBack: () -> Unit
) {
    val prefs by AppRepositories.profile.userProfilePrefs()
        .collectAsStateWithLifecycle(UserProfilePrefs.Default)
    val scope = rememberCoroutineScope()
    val options = listOf(
        ThemeOption(
            key = UserProfilePrefs.ThemeTechDark,
            titleRes = R.string.settings_theme_tech_dark,
            descRes = R.string.settings_theme_tech_dark_desc
        ),
        ThemeOption(
            key = UserProfilePrefs.ThemePaperLight,
            titleRes = R.string.settings_theme_paper_light,
            descRes = R.string.settings_theme_paper_light_desc
        ),
        ThemeOption(
            key = UserProfilePrefs.ThemeNeonPurpleBlue,
            titleRes = R.string.settings_theme_neon_purple_blue,
            descRes = R.string.settings_theme_neon_purple_blue_desc
        ),
        ThemeOption(
            key = UserProfilePrefs.ThemeForestGold,
            titleRes = R.string.settings_theme_forest_gold,
            descRes = R.string.settings_theme_forest_gold_desc
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeritageSecondaryTopBar(
                title = stringResource(R.string.settings_theme_title),
                onBack = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = ScreenLayout.HorizontalPadding)
                .padding(vertical = ScreenLayout.TopSpacing),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.settings_theme_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(options, key = { it.key }) { option ->
                val selected = prefs.themeKey == option.key
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(option.titleRes),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(option.descRes),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingContent = {
                        if (selected) {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    modifier = Modifier
                        .padding(vertical = 2.dp)
                        .clickable {
                            if (!selected) {
                                scope.launch {
                                    AppRepositories.profile.setThemeKey(option.key)
                                }
                            }
                        },
                    colors = ListItemDefaults.colors(
                        containerColor = if (selected) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                )
            }
        }
    }
}

private data class ThemeOption(
    val key: String,
    val titleRes: Int,
    val descRes: Int
)
