package com.guyunxinchuan.heritage.music

import android.content.Context

/**
 * 商城购物车未结算数量角标（本地持久化，与购物车页演示数据独立展示角标）。
 *
 * 加入内存缓存：每次 ShopActivity onResume 都会刷新角标，避免每次都同步磁盘读。
 */
object ShopCartStore {
    private const val PREFS = "heritage_shop_cart"
    private const val KEY_PENDING_COUNT = "pending_item_count"

    @Volatile private var cached: Int = -1

    fun getPendingCount(context: Context): Int {
        if (cached >= 0) return cached
        val sp = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val v = sp.getInt(KEY_PENDING_COUNT, 0)
        cached = v
        return v
    }

    fun addItems(context: Context, delta: Int) {
        if (delta <= 0) return
        val sp = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val current = if (cached >= 0) cached else sp.getInt(KEY_PENDING_COUNT, 0)
        val next = (current + delta).coerceAtLeast(0)
        cached = next
        sp.edit().putInt(KEY_PENDING_COUNT, next).apply()
    }

    /** 由 [App] 在子线程预热调用，避免冷启动时主线程触盘。 */
    internal fun warmUp(context: Context) {
        if (cached >= 0) return
        val sp = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        cached = sp.getInt(KEY_PENDING_COUNT, 0)
    }
}
