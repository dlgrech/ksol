package com.dgsd.android.solar.home

import androidx.annotation.DrawableRes
import com.dgsd.ksol.model.PublicKey

data class SendActionSheetItem(
  val displayText: CharSequence,
  @DrawableRes val iconRes: Int,
  val type: Type
) {

  sealed interface Type {
    object ScanQr : Type
    object EnterPublicAddress : Type
    object Nearby : Type
    data class PreselectedAddress(val address: PublicKey) : Type
  }
}