package com.intangibleheritage.music.core.network

import retrofit2.http.GET

/**
 * 后端接口；[musicHome] 在示例域名下通常会失败，由 [com.intangibleheritage.music.core.data.repository.RemoteMusicHallRepository] 降级为假数据。
 */
interface HeritageApi {

    @GET("music/home")
    suspend fun musicHome(): MusicHomeResponseDto

    /** 社区列表；示例域名下多失败，由 [com.intangibleheritage.music.core.data.repository.RemoteCommunityRepository] 降级为假数据。 */
    @GET("community/posts")
    suspend fun communityPosts(): CommunityPostsResponseDto
}
