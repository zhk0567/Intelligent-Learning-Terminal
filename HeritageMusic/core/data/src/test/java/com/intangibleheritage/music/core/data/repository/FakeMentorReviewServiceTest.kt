package com.intangibleheritage.music.core.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeMentorReviewServiceTest {

    private val service = FakeMentorReviewService()

    @Test
    fun review_scoresAreWithinExpectedRange() {
        val result = service.review(audioName = "a.wav", focus = "节奏")

        assertTrue(result.rhythmScore in 70..95)
        assertTrue(result.intonationScore in 68..95)
        assertTrue(result.expressionScore in 72..95)
    }

    @Test
    fun review_containsFocusAndSuggestions() {
        val result = service.review(audioName = "练习.wav", focus = "音准")

        assertTrue(result.summary.contains("音准"))
        assertEquals(3, result.suggestions.size)
    }
}
