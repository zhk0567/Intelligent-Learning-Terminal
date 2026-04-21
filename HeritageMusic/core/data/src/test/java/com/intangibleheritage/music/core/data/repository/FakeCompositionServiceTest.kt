package com.intangibleheritage.music.core.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeCompositionServiceTest {

    private val service = FakeCompositionService()

    @Test
    fun compose_clampsTempoWithinRange() {
        val low = service.compose(style = "国风", mood = "平静", tempoBpm = 30)
        val high = service.compose(style = "国风", mood = "平静", tempoBpm = 260)

        assertEquals(60, low.tempoBpm)
        assertEquals(180, high.tempoBpm)
    }

    @Test
    fun compose_returnsDeterministicIdAndHint() {
        val a = service.compose(style = "国风", mood = "昂扬", tempoBpm = 96)
        val b = service.compose(style = "国风", mood = "昂扬", tempoBpm = 96)

        assertEquals(a.id, b.id)
        assertEquals(a.clipName, b.clipName)
        assertTrue(a.clipHint.contains("国风"))
        assertTrue(a.clipHint.contains("昂扬"))
    }
}
