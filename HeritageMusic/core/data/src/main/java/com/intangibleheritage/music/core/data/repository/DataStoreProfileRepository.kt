package com.intangibleheritage.music.core.data.repository

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.intangibleheritage.music.core.data.model.ProfileGridItem
import com.intangibleheritage.music.core.data.model.UserProfilePrefs
import com.intangibleheritage.music.core.data.profile.ProfileVisuals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

private val Application.profileStore: DataStore<Preferences> by preferencesDataStore(name = "heritage_profile")

class DataStoreProfileRepository(
    private val application: Application
) : ProfileRepository {

    private val dataStore get() = application.profileStore

    private object Keys {
        val favorites = stringSetPreferencesKey("favorites")
        val history = stringPreferencesKey("history_ids")
        val nickname = stringPreferencesKey("user_nickname")
        val avatarKey = stringPreferencesKey("user_avatar_key")
        val themeKey = stringPreferencesKey("user_theme_key")
    }

    private val allowedAvatarKeys = setOf(
        UserProfilePrefs.AvatarDefault,
        UserProfilePrefs.AvatarPick1,
        UserProfilePrefs.AvatarPick2,
        UserProfilePrefs.AvatarPick3
    )
    private val allowedThemeKeys = setOf(
        UserProfilePrefs.ThemeTechDark,
        UserProfilePrefs.ThemePaperLight,
        UserProfilePrefs.ThemeNeonPurpleBlue,
        UserProfilePrefs.ThemeForestGold
    )

    override fun favorites(): Flow<List<ProfileGridItem>> =
        dataStore.data.map { prefs ->
            val ids = prefs[Keys.favorites] ?: emptySet()
            ids.map { ProfileGridItem(it, ProfileVisuals.thumbnailForContentId(it)) }
        }
            .distinctUntilChanged { a, b -> sameGridOrderById(a, b) }
            .flowOn(Dispatchers.Default)

    override fun watchHistory(): Flow<List<ProfileGridItem>> =
        dataStore.data.map { prefs ->
            val raw = prefs[Keys.history].orEmpty()
            if (raw.isBlank()) emptyList()
            else raw.split(',').filter { it.isNotBlank() }.distinct()
                .map { ProfileGridItem(it, ProfileVisuals.thumbnailForContentId(it)) }
        }
            .distinctUntilChanged { a, b -> sameGridOrderById(a, b) }
            .flowOn(Dispatchers.Default)

    override fun favoriteIds(): Flow<Set<String>> =
        dataStore.data.map { prefs -> prefs[Keys.favorites] ?: emptySet() }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)

    override fun userProfilePrefs(): Flow<UserProfilePrefs> =
        dataStore.data.map { prefs ->
            val rawKey = prefs[Keys.avatarKey].orEmpty()
            val key = rawKey.takeIf { it in allowedAvatarKeys } ?: UserProfilePrefs.AvatarDefault
            val rawTheme = prefs[Keys.themeKey].orEmpty()
            val themeKey = rawTheme.takeIf { it in allowedThemeKeys } ?: UserProfilePrefs.ThemeTechDark
            UserProfilePrefs(
                nickname = prefs[Keys.nickname].orEmpty(),
                avatarKey = key,
                themeKey = themeKey
            )
        }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)

    override suspend fun toggleFavorite(contentId: String) {
        dataStore.edit { p ->
            val cur = p[Keys.favorites] ?: emptySet()
            p[Keys.favorites] = if (contentId in cur) cur - contentId else cur + contentId
        }
    }

    override suspend fun addHistory(contentId: String) {
        dataStore.edit { p ->
            val cur = p[Keys.history].orEmpty()
            val list = if (cur.isBlank()) mutableListOf() else cur.split(',').filter { it.isNotBlank() }.toMutableList()
            list.remove(contentId)
            list.add(0, contentId)
            while (list.size > 50) list.removeAt(list.lastIndex)
            p[Keys.history] = list.joinToString(",")
        }
    }

    override suspend fun setUserNickname(nickname: String) {
        val trimmed = nickname.trim().take(NicknameMaxLen)
        dataStore.edit { it[Keys.nickname] = trimmed }
    }

    override suspend fun setUserAvatarKey(avatarKey: String) {
        if (avatarKey !in allowedAvatarKeys) return
        dataStore.edit { it[Keys.avatarKey] = avatarKey }
    }

    override suspend fun setThemeKey(themeKey: String) {
        if (themeKey !in allowedThemeKeys) return
        dataStore.edit { it[Keys.themeKey] = themeKey }
    }

    override suspend fun removeHistoryItem(contentId: String) {
        val id = contentId.trim()
        if (id.isEmpty()) return
        dataStore.edit { p ->
            val cur = p[Keys.history].orEmpty()
            if (cur.isBlank()) return@edit
            val list = cur.split(',').filter { it.isNotBlank() && it != id }.toMutableList()
            p[Keys.history] = list.joinToString(",")
        }
    }

    override suspend fun clearWatchHistory() {
        dataStore.edit { it.remove(Keys.history) }
    }

    private companion object {
        const val NicknameMaxLen = 24

        fun sameGridOrderById(a: List<ProfileGridItem>, b: List<ProfileGridItem>): Boolean {
            if (a.size != b.size) return false
            for (i in a.indices) {
                if (a[i].id != b[i].id) return false
            }
            return true
        }
    }
}
