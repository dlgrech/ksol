package com.dgsd.android.solar.extensions

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.annotation.FloatRange

fun View.performHapticFeedback() {
  performHapticFeedback(
    HapticFeedbackConstants.VIRTUAL_KEY_RELEASE,
    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
  )
}

fun View.blur(@FloatRange(from = 0.0, to = 1.0) blurAmount: Float) {
  if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
    setRenderEffect(
      RenderEffect.createBlurEffect(
        30 * blurAmount,
        30 * blurAmount,
        Shader.TileMode.MIRROR
      )
    )
  }
}