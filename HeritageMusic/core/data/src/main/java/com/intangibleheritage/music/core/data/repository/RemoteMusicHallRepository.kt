package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.MusicHallHomeData
import com.intangibleheritage.music.core.network.HeritageApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 拉取 [HeritageApi.musicHome] 并与假数据合并；失败或示例域名不可达时整页回退为 [FakeMusicHallRepository]。
 */
class RemoteMusicHallRepository(
    private val api: HeritageApi
) : MusicHallRepository {

    private val fallback = FakeMusicHallRepository()

    override suspend fun loadHome(): MusicHallHomeData = withContext(Dispatchers.IO) {
        val fake = fallback.loadHome()
        try {
            val dto = api.musicHome()
            MusicHallDtoMapper.merge(dto, fake)
        } catch (_: Exception) {
            fake.copy(usedRemoteFallback = true)
        }
    }
}
