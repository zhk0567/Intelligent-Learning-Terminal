package com.intangibleheritage.music.core.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeInteractionAnalyticsRepositoryTest {

    @Test
    fun track_updatesAllCounters() {
        val repo = FakeInteractionAnalyticsRepository()

        repo.trackComposeGenerated()
        repo.trackComposeListenClicked()
        repo.trackComposeCollectClicked()
        repo.trackReviewGenerated()

        val snapshot = repo.snapshot()
        assertEquals(1, snapshot.composeGenerateCount)
        assertEquals(1, snapshot.composeListenCount)
        assertEquals(1, snapshot.composeCollectCount)
        assertEquals(1, snapshot.reviewGenerateCount)
    }

    @Test
    fun track_recordsRecentEventsInReverseOrder() {
        val repo = FakeInteractionAnalyticsRepository()

        repo.trackComposeGenerated()
        repo.trackComposeListenClicked()

        val snapshot = repo.snapshot()
        assertEquals(2, snapshot.recentEvents.size)
        assertEquals("编曲试听", snapshot.recentEvents.first().action)
        assertEquals("编曲生成", snapshot.recentEvents.last().action)
        assertTrue(snapshot.recentEvents.first().timestampMs > 0L)
    }
}
