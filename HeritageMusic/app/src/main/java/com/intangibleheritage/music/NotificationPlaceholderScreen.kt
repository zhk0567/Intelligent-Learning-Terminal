package com.intangibleheritage.music

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.intangibleheritage.music.core.resources.R
import com.intangibleheritage.music.core.ui.navigation.InvalidDeepLinkScreen

/** P1-4：消息中心占位，后续可换为真实通知列表。 */
@Composable
fun NotificationPlaceholderScreen(onBack: () -> Unit) {
    InvalidDeepLinkScreen(
        title = stringResource(R.string.notifications_title),
        message = stringResource(R.string.notifications_empty),
        onBack = onBack
    )
}
