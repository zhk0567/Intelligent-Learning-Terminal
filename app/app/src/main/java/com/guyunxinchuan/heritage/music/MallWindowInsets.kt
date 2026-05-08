package com.guyunxinchuan.heritage.music

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * 全 App 统一边衬：状态栏透明 + 顶区 statusBars.top；
 * 系统手势/导航条底 inset 只加在「最底层」：有自定义底栏时只抬底栏（layout margin），
 * 列表/滚动区不再叠加 system bottom，避免双计并与固定高度底栏被系统栏压住。
 */
object MallWindowInsets {

    private fun View.ensureBasePaddingRecord() {
        if (getTag(R.id.tag_inset_base_padding) == null) {
            setTag(
                R.id.tag_inset_base_padding,
                intArrayOf(paddingLeft, paddingTop, paddingRight, paddingBottom),
            )
        }
    }

    private fun View.basePadding(): IntArray {
        @Suppress("UNCHECKED_CAST")
        return getTag(R.id.tag_inset_base_padding) as IntArray
    }

    private fun View.ensureBaseMarginBottomRecord(lp: ViewGroup.MarginLayoutParams) {
        if (getTag(R.id.tag_inset_base_margin_bottom) == null) {
            setTag(R.id.tag_inset_base_margin_bottom, lp.bottomMargin)
        }
    }

    private fun View.baseMarginBottom(): Int =
        getTag(R.id.tag_inset_base_margin_bottom) as Int

    /** 导航条/手势条底部安全区（displayCutout 与 systemBars 取较大底边） */
    private fun navigationBottomInset(insets: WindowInsetsCompat): Int {
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val nav = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        val cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout())
        return max(max(bars.bottom, nav.bottom), cutout.bottom)
    }

    private fun applyBottomBarInset(bar: View, insetBottom: Int) {
        val lp = bar.layoutParams as? ViewGroup.MarginLayoutParams
        if (lp != null) {
            bar.ensureBaseMarginBottomRecord(lp)
            lp.bottomMargin = bar.baseMarginBottom() + insetBottom
            bar.ensureBasePaddingRecord()
            val b = bar.basePadding()
            bar.setPadding(b[0], b[1], b[2], b[3])
            bar.requestLayout()
        } else {
            bar.ensureBasePaddingRecord()
            val b = bar.basePadding()
            bar.setPadding(b[0], b[1], b[2], b[3] + insetBottom)
        }
    }

    fun applyToActivity(
        activity: AppCompatActivity,
        root: View,
        topInsetHost: View?,
        bottomBar: View? = null,
        scrollOrListBottomInset: View? = null,
        scrollBottomPaddingDp: Float = 8f,
    ) {
        val window = activity.window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // statusBarColor 在 API 35+ 已被声明为 deprecated；在更早的 API 上仍生效，且与
        // setDecorFitsSystemWindows(false) 配合实现状态栏透明，这里显式抑制告警。
        @Suppress("DEPRECATION")
        window.statusBarColor = Color.TRANSPARENT
        val density = root.resources.displayMetrics.density
        val baseBottom = (scrollBottomPaddingDp * density).roundToInt()
        val hasBottomBar = bottomBar != null
        topInsetHost?.ensureBasePaddingRecord()
        scrollOrListBottomInset?.ensureBasePaddingRecord()

        ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val navBottom = navigationBottomInset(insets)
            val scrollBottomSystem = if (hasBottomBar) 0 else navBottom

            when {
                topInsetHost != null &&
                    scrollOrListBottomInset != null &&
                    topInsetHost === scrollOrListBottomInset -> {
                    val b = topInsetHost.basePadding()
                    topInsetHost.setPadding(
                        b[0],
                        b[1] + sys.top,
                        b[2],
                        b[3] + baseBottom + scrollBottomSystem,
                    )
                }
                else -> {
                    topInsetHost?.let { v ->
                        val b = v.basePadding()
                        v.setPadding(b[0], b[1] + sys.top, b[2], b[3])
                    }
                    scrollOrListBottomInset?.let { v ->
                        val b = v.basePadding()
                        v.setPadding(
                            b[0],
                            b[1],
                            b[2],
                            b[3] + baseBottom + scrollBottomSystem,
                        )
                    }
                }
            }

            bottomBar?.let { bar -> applyBottomBarInset(bar, navBottom) }
            insets
        }
        root.post { ViewCompat.requestApplyInsets(root) }
    }

    /**
     * 单根布局（无底部自定义导航）：整页同时加状态栏 top 与手势区 bottom。
     * 典型：`setContentView` 的根 `ScrollView` / `ConstraintLayout`。
     */
    fun applyRootOnly(
        activity: AppCompatActivity,
        contentRoot: View,
        bottomExtraDp: Float = 0f,
    ) {
        applyToActivity(activity, contentRoot, contentRoot, null, contentRoot, bottomExtraDp)
    }

    /** [setContentView] 后根节点，即 `android.R.id.content` 的直接子 View。 */
    fun contentRoot(activity: AppCompatActivity): View =
        (activity.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)

    fun applyToContentRoot(activity: AppCompatActivity, bottomExtraDp: Float = 0f) {
        applyRootOnly(activity, contentRoot(activity), bottomExtraDp)
    }
}
