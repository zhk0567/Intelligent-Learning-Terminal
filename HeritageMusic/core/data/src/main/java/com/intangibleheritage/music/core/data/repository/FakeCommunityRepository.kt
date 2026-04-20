package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.CommunityCategory
import com.intangibleheritage.music.core.data.model.CommunityPost
import com.intangibleheritage.music.core.resources.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FakeCommunityRepository : CommunityRepository {

    private val all = listOf(
        CommunityPost(
            "c1",
            R.string.post_heritage_electronic,
            R.string.post_heritage_electronic_sub,
            CommunityCategory.FolkInstrument,
            R.string.post_body_c1
        ),
        CommunityPost(
            "c2",
            R.string.post_niche_heritage,
            R.string.post_niche_heritage_sub,
            CommunityCategory.Electronic,
            R.string.post_body_c2
        ),
        CommunityPost(
            "c3",
            R.string.post_self_animation,
            R.string.post_self_animation_sub,
            CommunityCategory.Ai,
            R.string.post_body_c3
        ),
        CommunityPost(
            "c4",
            R.string.post_visit_artists,
            R.string.post_visit_artists_sub,
            CommunityCategory.FolkInstrument,
            R.string.post_body_c4
        )
    )

    override suspend fun posts(category: CommunityCategory?): List<CommunityPost> = withContext(Dispatchers.Default) {
        if (category == null) all
        else all.filter { it.category == category }
    }

    override fun postById(id: String): CommunityPost? = all.find { it.id == id }
}
