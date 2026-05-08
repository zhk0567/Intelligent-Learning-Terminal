package com.guyunxinchuan.heritage.music

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

/**
 * 统一替代 `override fun onBackPressed()`：
 * 该 API 在新 SDK 已 deprecated，且与 Android 13+ predictive back 不兼容。
 * 推荐通过 OnBackPressedDispatcher 注册回调，由系统侧自动管理生命周期。
 *
 * 使用方式：
 * ```kotlin
 * override fun onCreate(savedInstanceState: Bundle?) {
 *     super.onCreate(savedInstanceState)
 *     handleBack { finish() }     // 或自定义动作
 * }
 * ```
 */
fun AppCompatActivity.handleBack(action: () -> Unit) {
    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            action()
        }
    })
}
