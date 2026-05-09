package com.guyunxinchuan.heritage.music

import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.dispose
import coil.load
import coil.size.Scale

/**
 * 统一的封面/图片加载入口。背后用 Coil：
 *   - 工作线程解码，避免主线程长解码大 PNG（如 425KB 的 logo.png）；
 *   - 按 ImageView 实测尺寸缩到合适像素，省内存与 GPU 上传时间；
 *   - 关闭 crossfade，列表/复用滚动更稳；
 *   - 允许 RGB_565，进一步降低位图内存。
 *
 * preset 仅作为 ImageView 尚未测量时的兜底尺寸（dp -> px 在调用处折算）。
 */
enum class CoverPreset(val targetDpW: Int, val targetDpH: Int) {
    /** 列表里的方/横向卡片缩略 */
    Card(220, 180),

    /** 大型横向 banner */
    Banner(360, 200),

    /** 列表里的小头像/小封面 */
    Thumb(96, 96),

    /** 详情页主图 */
    Hero(360, 240),
}

/** 当 `remoteUrl` 非空且已配置静态资源域名时从网络加载，否则加载 [fallbackResId]。 */
fun ImageView.loadCoverRemoteOrDrawable(
    remoteUrl: String?,
    @DrawableRes fallbackResId: Int,
    preset: CoverPreset = CoverPreset.Card,
    @DrawableRes placeholder: Int = R.drawable.music_cover_placeholder,
    @DrawableRes error: Int = R.drawable.music_cover_placeholder,
    centerCrop: Boolean = true,
) {
    val url = remoteUrl?.trim()?.takeIf { it.isNotEmpty() }
    if (url == null) {
        loadCover(fallbackResId, preset, placeholder, error, centerCrop)
        return
    }
    val dm = resources.displayMetrics
    val measuredW = if (width > 0) width else (preset.targetDpW * dm.density).toInt()
    val measuredH = if (height > 0) height else (preset.targetDpH * dm.density).toInt()
    val safeW = measuredW.coerceIn(64, 1080)
    val safeH = measuredH.coerceIn(64, 1080)
    load(url) {
        size(safeW, safeH)
        scale(if (centerCrop) Scale.FILL else Scale.FIT)
        crossfade(false)
        allowRgb565(true)
        placeholder(fallbackResId)
        error(error)
    }
}

fun ImageView.loadCover(
    @DrawableRes resId: Int,
    preset: CoverPreset = CoverPreset.Card,
    @DrawableRes placeholder: Int = R.drawable.music_cover_placeholder,
    @DrawableRes error: Int = R.drawable.music_cover_placeholder,
    centerCrop: Boolean = true,
) {
    val dm = resources.displayMetrics
    val measuredW = if (width > 0) width else (preset.targetDpW * dm.density).toInt()
    val measuredH = if (height > 0) height else (preset.targetDpH * dm.density).toInt()
    val safeW = measuredW.coerceIn(64, 1080)
    val safeH = measuredH.coerceIn(64, 1080)
    load(resId) {
        size(safeW, safeH)
        scale(if (centerCrop) Scale.FILL else Scale.FIT)
        crossfade(false)
        allowRgb565(true)
        placeholder(placeholder)
        error(error)
    }
}

/** Adapter 在 onViewRecycled 调用，避免后台解码完毕回写到错位的 Holder。 */
fun ImageView.cancelCoverLoad() {
    dispose()
    setImageDrawable(null)
}
