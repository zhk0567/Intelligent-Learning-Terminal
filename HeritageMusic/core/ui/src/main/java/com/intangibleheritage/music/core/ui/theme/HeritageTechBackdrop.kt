package com.intangibleheritage.music.core.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * 全屏科技风底：径向光晕 + 极疏网格线（[drawWithCache] 避免每帧重复分配 Brush/Path）。
 * 网格已刻意稀疏以降低滑动时 GPU 线段绘制压力。
 */
@Composable
fun HeritageTechBackdrop(
    modifier: Modifier = Modifier,
    simplified: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .drawWithCache {
                val w = size.width
                val h = size.height
                if (w <= 0f || h <= 0f) {
                    return@drawWithCache onDrawBehind { /* no-op */ }
                }
                val bg = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A1020),
                        Background,
                        Color(0xFF02040A)
                    ),
                    startY = 0f,
                    endY = h
                )
                val glow1 = Brush.radialGradient(
                    colors = listOf(
                        PrimaryTeal.copy(alpha = 0.14f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.5f, h * 0.12f),
                    radius = w * 0.85f
                )
                val glow2 = Brush.radialGradient(
                    colors = listOf(
                        AccentViolet.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.85f, h * 0.55f),
                    radius = h * 0.45f
                )
                val step = if (simplified) 220.dp.toPx() else 160.dp.toPx()
                val gridPath = Path().apply {
                    var x = 0f
                    while (x <= w) {
                        moveTo(x, 0f)
                        lineTo(x, h)
                        x += step
                    }
                    var y = 0f
                    while (y <= h) {
                        moveTo(0f, y)
                        lineTo(w, y)
                        y += step
                    }
                }
                val gridStroke = Stroke(width = 1f)
                val bottomGlowY = h * 0.92f
                onDrawBehind {
                    drawRect(brush = bg, size = size)
                    drawCircle(
                        brush = glow1,
                        radius = if (simplified) w * 0.72f else w * 0.85f,
                        center = Offset(w * 0.5f, h * 0.12f)
                    )
                    if (!simplified) {
                        drawCircle(brush = glow2, radius = h * 0.45f, center = Offset(w * 0.85f, h * 0.55f))
                        drawPath(gridPath, color = TechGridLine, style = gridStroke)
                    }
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                PrimaryTeal.copy(alpha = 0.12f),
                                Color.Transparent
                            ),
                            startX = 0f,
                            endX = w
                        ),
                        start = Offset(0f, bottomGlowY),
                        end = Offset(w, bottomGlowY),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }
    )
}
