package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.MusicHallHomeData

interface MusicHallRepository {

    /**
     * 加载音乐馆首页区块；远程实现可能走网络并在失败时降级为假数据。
     */
    suspend fun loadHome(): MusicHallHomeData
}
