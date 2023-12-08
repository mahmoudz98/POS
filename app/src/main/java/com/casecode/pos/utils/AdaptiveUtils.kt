package com.casecode.pos.utils

import android.app.Activity
import androidx.window.layout.WindowMetrics
import androidx.window.layout.WindowMetricsCalculator


const val MEDIUM_SCREEN_WIDTH_SIZE = 600
const val LARGE_SCREEN_WIDTH_SIZE = 1240

/**
 * Determines whether the device has a compact screen.
 */
fun Activity.compactScreen(): Boolean
{
   val screenMetrics: WindowMetrics = WindowMetricsCalculator.getOrCreate()
      .computeMaximumWindowMetrics(this)
   val shortSide: Int = Math.min(screenMetrics.bounds.width(),
      screenMetrics.bounds.height())
   return shortSide / this.resources.displayMetrics.density < MEDIUM_SCREEN_WIDTH_SIZE
}


