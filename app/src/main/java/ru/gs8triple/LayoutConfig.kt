package ru.gs8triple

import android.graphics.Rect
import kotlin.math.roundToInt

data class LayoutConfig(
    val windowCount: Int = 3,
    val leftWidth: Int = 400,
    val centerWidth: Int = 760,
    val screenWidth: Int = 1920,
    val screenHeight: Int = 968
) {
    fun bounds(): List<Rect> {
        val w = screenWidth.coerceAtLeast(1)
        val h = screenHeight.coerceAtLeast(1)
        if (windowCount == 2) {
            val min = (w * 0.20f).roundToInt().coerceAtLeast(1)
            val left = leftWidth.coerceIn(min, (w - min).coerceAtLeast(min))
            return listOf(Rect(0, 0, left, h), Rect(left, 0, w, h))
        }

        val minPanel = (w * 0.12f).roundToInt().coerceAtLeast(1)
        val left = leftWidth.coerceIn(minPanel, w - 2 * minPanel)
        val center = centerWidth.coerceIn(minPanel, w - left - minPanel)
        val right = w - left - center
        return listOf(
            Rect(0, 0, left, h),
            Rect(left, 0, left + center, h),
            Rect(left + center, 0, w, h)
        )
    }
}
