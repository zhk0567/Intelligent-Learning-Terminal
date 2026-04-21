package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.ProfileGridItem
import com.intangibleheritage.music.core.data.model.UserProfilePrefs
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun favorites(): Flow<List<ProfileGridItem>>
    fun watchHistory(): Flow<List<ProfileGridItem>>
    fun favoriteIds(): Flow<Set<String>>
    fun userProfilePrefs(): Flow<UserProfilePrefs>
    suspend fun toggleFavorite(contentId: String)
    suspend fun addHistory(contentId: String)
    suspend fun setUserNickname(nickname: String)
    suspend fun setUserAvatarKey(avatarKey: String)
    suspend fun setThemeKey(themeKey: String)
    /** 从观看历史中移除一条（收藏不受影响） */
    suspend fun removeHistoryItem(contentId: String)
    /** 清空全部观看历史 */
    suspend fun clearWatchHistory()
}
