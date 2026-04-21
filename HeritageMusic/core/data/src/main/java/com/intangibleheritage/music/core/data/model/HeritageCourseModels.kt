package com.intangibleheritage.music.core.data.model

import androidx.annotation.DrawableRes

enum class CourseLevel {
    Basic,
    Advanced,
    Research
}

data class HeritageCourse(
    val id: String,
    val title: String,
    val tutorName: String,
    val level: CourseLevel,
    val lessons: Int,
    val summary: String,
    val goals: List<String>,
    val copyrightNote: String,
    @DrawableRes val coverRes: Int,
    val relatedStoryId: String? = null,
    val relatedTrackId: String? = null
)
