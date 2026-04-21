package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.MentorReviewResult
import kotlin.math.absoluteValue

class FakeMentorReviewService : MentorReviewService {
    override fun review(audioName: String, focus: String): MentorReviewResult {
        val seed = "${audioName}_$focus".hashCode().absoluteValue
        val rhythm = 70 + seed % 26
        val intonation = 68 + (seed / 3) % 28
        val expression = 72 + (seed / 5) % 24
        val summary = "针对「$audioName」已完成示例点评，当前优势在表现力，建议优先优化$focus。"
        return MentorReviewResult(
            id = "review_$seed",
            rhythmScore = rhythm.coerceAtMost(100),
            intonationScore = intonation.coerceAtMost(100),
            expressionScore = expression.coerceAtMost(100),
            summary = summary,
            suggestions = listOf(
                "先用慢速节拍器分段练习，确保起拍稳定。",
                "对长音句尾做呼吸控制，避免尾音下坠。",
                "在重复段加入轻微力度层次，突出情绪变化。"
            )
        )
    }
}
