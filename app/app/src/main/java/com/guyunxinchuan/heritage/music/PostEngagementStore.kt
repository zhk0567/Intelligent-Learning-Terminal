package com.guyunxinchuan.heritage.music

import java.util.concurrent.ConcurrentHashMap

/**
 * 创作社区列表与帖子详情页之间：点赞数、评论数、点赞态、收藏态的本地同步（演示用，无后端）。
 */
object PostEngagementStore {

    data class State(
        var likeCount: Int,
        var commentCount: Int,
        var liked: Boolean,
        var favorited: Boolean
    )

    private val byPostId = ConcurrentHashMap<Int, State>()

    /** 列表项：已有则返回同一实例（与详情、其它列表项共享）。 */
    fun stateForPost(post: Post): State = byPostId.getOrPut(post.id) {
        State(
            likeCount = post.likeCount,
            commentCount = post.commentCount,
            liked = false,
            favorited = false
        )
    }

    /**
     * 详情页进入：若用户在列表已点过赞/评过，沿用内存态；否则用 Intent 带入的初值建一条。
     */
    fun stateForDetail(
        postId: Int,
        like: Int,
        comment: Int,
        liked: Boolean,
        favorited: Boolean
    ): State = byPostId.getOrPut(postId) {
        State(likeCount = like, commentCount = comment, liked = liked, favorited = favorited)
    }
}
