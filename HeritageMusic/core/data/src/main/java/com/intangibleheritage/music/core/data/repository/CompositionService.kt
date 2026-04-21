package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.CompositionResult

interface CompositionService {
    fun compose(style: String, mood: String, tempoBpm: Int): CompositionResult
}
