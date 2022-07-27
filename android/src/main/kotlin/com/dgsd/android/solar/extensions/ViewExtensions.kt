package com.dgsd.android.solar.extensions

import android.graphics.Outline
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.Px

fun View.performHapticFeedback() {
  performHapticFeedback(
    HapticFeedbackConstants.VIRTUAL_KEY_RELEASE,
    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
  )
}

fun View.roundedCorners(@Px radius: Float) {
  clipToOutline = true
  outlineProvider = object : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline?) {
      outline?.setRoundRect(0, 0, view.width, view.height, radius)
    }
  }
}