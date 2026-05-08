package com.guyunxinchuan.heritage.music

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * 为 [StaggeredGridLayoutManager] 的列之间、行之间增加间距（列间距均分在相邻 item 的 left/right）。
 */
class StaggeredGridSpacingDecoration(
    private val spanCount: Int,
    private val columnSpacingPx: Int,
    private val rowSpacingPx: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val lp = view.layoutParams as? StaggeredGridLayoutManager.LayoutParams ?: return
        if (lp.isFullSpan) {
            outRect.set(0, 0, 0, rowSpacingPx)
            return
        }
        // 未分配列前 spanIndex 为 -1，若强行当成 0 会导致右列 item 水平 inset 错误、两列图片竖边不齐
        val spanIndex = lp.spanIndex
        if (spanIndex < 0 || spanIndex >= spanCount) {
            outRect.set(0, 0, 0, rowSpacingPx)
            return
        }
        outRect.left = columnSpacingPx * spanIndex / spanCount
        outRect.right = columnSpacingPx - (spanIndex + 1) * columnSpacingPx / spanCount
        outRect.top = 0
        outRect.bottom = rowSpacingPx
    }
}
