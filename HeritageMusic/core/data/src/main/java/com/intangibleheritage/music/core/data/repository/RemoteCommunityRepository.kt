package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.CommunityCategory
import com.intangibleheritage.music.core.data.model.CommunityPost
import com.intangibleheritage.music.core.network.HeritageApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicReference

/**
 * 拉取 [HeritageApi.communityPosts]；失败、空列表或解析无有效项时回退 [FakeCommunityRepository]。
 */
class RemoteCommunityRepository(
    private val api: HeritageApi
) : CommunityRepository {

    private val fallback = FakeCommunityRepository()
    private val lastRemotePosts = AtomicReference<List<CommunityPost>?>(null)

    override suspend fun posts(category: CommunityCategory?): List<CommunityPost> = withContext(Dispatchers.IO) {
        try {
            val dto = api.communityPosts()
            val mapped = CommunityDtoMapper.mapPosts(dto)
            if (!mapped.isNullOrEmpty()) {
                val filtered = if (category == null) {
                    mapped
                } else {
                    mapped.filter { it.category == category }
                }
                if (filtered.isNotEmpty()) {
                    lastRemotePosts.set(mapped)
                    return@withContext filtered
                }
            }
        } catch (_: Exception) {
            // fall through
        }
        lastRemotePosts.set(null)
        fallback.posts(category)
    }

    override fun postById(id: String): CommunityPost? =
        lastRemotePosts.get()?.find { it.id == id } ?: fallback.postById(id)
}
