package com.guyunxinchuan.heritage.music

import android.content.Context
import android.widget.Toast

/**
 * 统一短时提示：取消上一条再显示，避免连点堆积、文案不刷新。
 */
object UiFeedback {
    @Volatile
    private var activeToast: Toast? = null

    @Synchronized
    fun toast(context: Context, message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
        val app = context.applicationContext
        activeToast?.cancel()
        activeToast = Toast.makeText(app, message, duration).also { it.show() }
    }
}
