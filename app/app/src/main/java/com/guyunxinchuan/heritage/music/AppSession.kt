package com.guyunxinchuan.heritage.music

import android.content.Context

/**
 * 轻量会话：游客 / 已登录（演示用 SharedPreferences）。
 *
 * 加入内存缓存：避免每次 onCreate / onResume 都去触磁盘 I/O。
 * 首次访问从 SP 装载一次；之后所有读操作走 `cached`，写操作同时更新内存与 SP。
 */
object AppSession {
    private const val PREFS = "heritage_app_session"
    private const val KEY_IS_GUEST = "is_guest"

    @Volatile private var cached: Boolean? = null

    fun isGuest(context: Context): Boolean {
        cached?.let { return it }
        val v = context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_IS_GUEST, false)
        cached = v
        return v
    }

    fun setGuest(context: Context, guest: Boolean) {
        cached = guest
        context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_IS_GUEST, guest)
            .apply()
    }

    fun markLoggedInUser(context: Context) {
        setGuest(context, false)
    }

    /** 由 [App] 在子线程预热调用，避免冷启动时主线程触盘。 */
    internal fun warmUp(context: Context) {
        if (cached != null) return
        val v = context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_IS_GUEST, false)
        cached = v
    }
}
