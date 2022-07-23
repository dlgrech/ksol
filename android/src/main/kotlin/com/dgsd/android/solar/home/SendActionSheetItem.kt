package com.dgsd.android.solar.home

import androidx.annotation.DrawableRes

data class SendActionSheetItem(
  val displayText: CharSequence,
  @DrawableRes val iconRes: Int,
  val type: Type
) {

  enum class Type {
    SCAN_QR,
    ENTER_PUBLIC_ADDRESS,
    HISTORICAL_ADDRESS,
    NEARBY,
  }
}