package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.CourseLevel
import com.intangibleheritage.music.core.data.model.MallSection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeDomainRepositoriesTest {

    @Test
    fun mallRepository_filtersProductsBySection() {
        val repo = FakeMallRepository()

        val storyProducts = repo.productsInSection(MallSection.Story)
        assertEquals(3, storyProducts.size)
        assertTrue(storyProducts.all { it.section == MallSection.Story })
    }

    @Test
    fun mallRepository_returnsNullForUnknownId() {
        val repo = FakeMallRepository()

        assertNull(repo.productById("not_exists"))
    }

    @Test
    fun courseRepository_returnsAllThreeLevels() {
        val repo = FakeCourseRepository()

        val levels = repo.allCourses().map { it.level }.toSet()
        assertEquals(setOf(CourseLevel.Basic, CourseLevel.Advanced, CourseLevel.Research), levels)
    }

    @Test
    fun courseRepository_findsCourseById() {
        val repo = FakeCourseRepository()

        val course = repo.courseById("course_basic_guqin")
        assertNotNull(course)
        assertEquals("沈墨", course?.tutorName)
    }
}
