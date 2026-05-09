package com.guyunxinchuan.heritage.music

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

/**
 * 全局 Application：在后台线程预热常用的本地存储读取，避免冷启动时
 * 主线程上首次读取 SharedPreferences 触发磁盘 I/O 卡顿。
 */
class App : Application() {

    override fun onCreate() {
        AppCompatDelegate.setDefaultNightMode(ThemePrefs.getNightMode(this))
        super.onCreate()
        PlayerAudioEngine.setOnPlaybackComplete {
            PlayerSyncState.updatePlayingState(false)
            PlayerSyncState.currentPositionMs =
                PlayerSyncState.trackDurationMs.coerceAtLeast(0)
        }
        Thread({
            AppSession.warmUp(this)
            ShopCartStore.warmUp(this)
            ThemePrefs.warmUp(this)
        }, "app-warmup").apply {
            priority = Thread.MIN_PRIORITY
            isDaemon = true
            start()
        }
    }
}
