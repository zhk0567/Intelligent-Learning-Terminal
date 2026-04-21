package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.InteractionAnalyticsSnapshot
import com.intangibleheritage.music.core.data.model.InteractionEvent
import java.util.concurrent.atomic.AtomicInteger

class FakeInteractionAnalyticsRepository : InteractionAnalyticsRepository {
    private val maxEvents = 30
    private val events = ArrayDeque<InteractionEvent>()
    private val composeGenerate = AtomicInteger(0)
    private val composeListen = AtomicInteger(0)
    private val composeCollect = AtomicInteger(0)
    private val reviewGenerate = AtomicInteger(0)

    override fun trackComposeGenerated() {
        composeGenerate.incrementAndGet()
        appendEvent("编曲生成")
    }

    override fun trackComposeListenClicked() {
        composeListen.incrementAndGet()
        appendEvent("编曲试听")
    }

    override fun trackComposeCollectClicked() {
        composeCollect.incrementAndGet()
        appendEvent("编曲收藏")
    }

    override fun trackReviewGenerated() {
        reviewGenerate.incrementAndGet()
        appendEvent("点评生成")
    }

    override fun snapshot(): InteractionAnalyticsSnapshot = InteractionAnalyticsSnapshot(
        composeGenerateCount = composeGenerate.get(),
        composeListenCount = composeListen.get(),
        composeCollectCount = composeCollect.get(),
        reviewGenerateCount = reviewGenerate.get(),
        recentEvents = synchronized(events) { events.toList().asReversed() }
    )

    private fun appendEvent(action: String) {
        synchronized(events) {
            events.addLast(InteractionEvent(action = action, timestampMs = System.currentTimeMillis()))
            while (events.size > maxEvents) {
                events.removeFirst()
            }
        }
    }
}
