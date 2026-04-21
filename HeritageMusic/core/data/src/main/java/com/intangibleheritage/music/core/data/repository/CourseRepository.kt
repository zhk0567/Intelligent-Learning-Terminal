package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.HeritageCourse

interface CourseRepository {
    fun allCourses(): List<HeritageCourse>
    fun courseById(id: String): HeritageCourse?
}
