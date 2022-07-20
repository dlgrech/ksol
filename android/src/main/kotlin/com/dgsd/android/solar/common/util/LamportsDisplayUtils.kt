package com.dgsd.android.solar.common.util

import android.content.Context
import androidx.annotation.ColorInt
import com.dgsd.android.solar.R
import com.dgsd.android.solar.extensions.getColorAttr
import com.dgsd.ksol.model.Lamports

@ColorInt
fun getTextColorForLamports(context: Context, lamports: Lamports): Int {
  return when {
    lamports == 0L -> context.getColorAttr(android.R.attr.textColorSecondary)
    lamports < 0L -> context.getColorAttr(android.R.attr.colorError)
    else -> context.getColorAttr(R.attr.colorTertiary)
  }
}