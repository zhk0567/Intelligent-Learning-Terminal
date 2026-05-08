package com.guyunxinchuan.heritage.music

import android.os.SystemClock
import android.view.View

private const val DEFAULT_INTERVAL_MS = 450L

/**
 * 防止连点导致重复 Toast / 重复弹窗 / 重复请求。
 */
fun View.setOnClickListenerThrottled(intervalMs: Long = DEFAULT_INTERVAL_MS, listener: (View) -> Unit) {
    var last = 0L
    setOnClickListener { v ->
        val now = SystemClock.elapsedRealtime()
        if (now - last < intervalMs) return@setOnClickListener
        last = now
        listener(v)
    }
}
