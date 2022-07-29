package com.dgsd.android.solar.home

import androidx.annotation.DrawableRes

data class ReceiveActionSheetItem(
  val displayText: CharSequence,
  @DrawableRes val iconRes: Int,
  val type: Type
) {

  enum class Type {
    SHARE_ADDRESS,
    REQUEST_AMOUNT,
  }
}