package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.MentorReviewResult

interface MentorReviewService {
    fun review(audioName: String, focus: String): MentorReviewResult
}
