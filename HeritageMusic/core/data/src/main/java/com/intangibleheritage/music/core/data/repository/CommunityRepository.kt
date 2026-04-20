package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.CommunityCategory
import com.intangibleheritage.music.core.data.model.CommunityPost

interface CommunityRepository {
    suspend fun posts(category: CommunityCategory?): List<CommunityPost>

    fun postById(id: String): CommunityPost?
}
