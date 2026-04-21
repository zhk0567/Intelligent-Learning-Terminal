package com.intangibleheritage.music.core.data.model

data class InteractionEvent(
    val action: String,
    val timestampMs: Long
)

data class InteractionAnalyticsSnapshot(
    val composeGenerateCount: Int,
    val composeListenCount: Int,
    val composeCollectCount: Int,
    val reviewGenerateCount: Int,
    val recentEvents: List<InteractionEvent>
)
