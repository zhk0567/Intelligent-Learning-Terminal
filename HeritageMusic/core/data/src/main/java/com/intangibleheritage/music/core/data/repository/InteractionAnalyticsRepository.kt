package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.InteractionAnalyticsSnapshot

interface InteractionAnalyticsRepository {
    fun trackComposeGenerated()
    fun trackComposeListenClicked()
    fun trackComposeCollectClicked()
    fun trackReviewGenerated()
    fun snapshot(): InteractionAnalyticsSnapshot
}
