package com.guyunxinchuan.heritage.music

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

/**
 * 浅色 / 深色。持久化到 SharedPreferences，与 [App.onCreate] 中
 * [AppCompatDelegate.setDefaultNightMode] 配合，在冷启动时即应用正确主题。
 */
object ThemePrefs {
    private const val PREFS = "heritage_ui_prefs"
    private const val KEY_NIGHT_MODE = "night_mode"

    fun getNightMode(context: Context): Int {
        val v = context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_YES)
        return when (v) {
            AppCompatDelegate.MODE_NIGHT_NO,
            AppCompatDelegate.MODE_NIGHT_YES -> v
            else -> AppCompatDelegate.MODE_NIGHT_YES
        }
    }

    fun setNightMode(context: Context, mode: Int) {
        context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_NIGHT_MODE, mode)
            .apply()
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    internal fun warmUp(context: Context) {
        // 仅确保 SP 已读；真正应用主题在 App.onCreate 主线程已调用 setDefaultNightMode
        getNightMode(context)
    }
}
