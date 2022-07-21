package com.dgsd.android.solar.extensions

import android.view.HapticFeedbackConstants
import android.view.View

fun View.performHapticFeedback() {
  performHapticFeedback(
    HapticFeedbackConstants.VIRTUAL_KEY_RELEASE,
    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
  )
}