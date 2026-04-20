package com.intangibleheritage.music

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.intangibleheritage.music.core.data.AppRepositories
import com.intangibleheritage.music.core.data.RuntimePerformanceConfig
import com.intangibleheritage.music.core.network.HeritageApi
import com.intangibleheritage.music.core.network.NetworkModule
import java.io.File

/**
 * Coil 2.7：[ImageLoaderFactory.newImageLoader] 无参数。
 * 在 memoryCache/diskCache 的 lambda 内须显式 [this@HeritageApplication]，避免与 Builder 接收者冲突。
 */
class HeritageApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        RuntimePerformanceConfig.enableFakeDelay = BuildConfig.ENABLE_FAKE_DELAY
        val retrofit = NetworkModule.createRetrofit(
            baseUrl = BuildConfig.API_BASE_URL,
            enableLogging = BuildConfig.ENABLE_NETWORK_LOGGING
        )
        val api = NetworkModule.api<HeritageApi>(retrofit)
        AppRepositories.initialize(this, BuildConfig.USE_REMOTE_API, api)
    }

    override fun newImageLoader(): ImageLoader {
        val app: Application = this
        return ImageLoader.Builder(app)
            .memoryCache {
                MemoryCache.Builder(app)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(File(app.cacheDir, "coil_disk_cache"))
                    .maxSizePercent(0.05)
                    .build()
            }
            .crossfade(false)
            .build()
    }
}
