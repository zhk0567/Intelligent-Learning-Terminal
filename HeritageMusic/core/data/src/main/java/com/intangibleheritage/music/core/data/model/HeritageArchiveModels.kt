package com.intangibleheritage.music.core.data.model

import androidx.annotation.DrawableRes

enum class ArchiveAssetType {
    Video,
    Audio,
    Text,
    Inheritor
}

data class HeritageArchiveAsset(
    val id: String,
    val title: String,
    val region: String,
    val genre: String,
    val era: String,
    val inheritor: String,
    val sourcePath: String,
    val sourceTimeline: List<String>,
    val copyrightStatus: String,
    val credibilityScore: Int,
    val type: ArchiveAssetType,
    @DrawableRes val coverRes: Int,
    val relatedStoryId: String? = null,
    val relatedTrackId: String? = null
)
