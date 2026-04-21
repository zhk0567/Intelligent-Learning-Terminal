package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.CompositionResult
import kotlin.math.absoluteValue

class FakeCompositionService : CompositionService {
    override fun compose(style: String, mood: String, tempoBpm: Int): CompositionResult {
        val normalizedTempo = tempoBpm.coerceIn(60, 180)
        val seed = "${style}_${mood}_$normalizedTempo".hashCode().absoluteValue
        val clipName = "demo_clip_${seed % 1000}"
        val hint = "已生成 ${style}·${mood} 的 ${normalizedTempo} BPM 片段，可试听并继续微调。"
        return CompositionResult(
            id = "compose_$seed",
            style = style,
            mood = mood,
            tempoBpm = normalizedTempo,
            clipName = clipName,
            clipHint = hint
        )
    }
}
