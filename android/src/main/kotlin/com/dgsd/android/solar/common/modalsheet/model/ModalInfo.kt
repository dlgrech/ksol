package com.dgsd.android.solar.common.modalsheet.model

data class ModalInfo(
  val title: CharSequence?,
  val message: CharSequence?,
  val positiveButton: ButtonInfo,
  val negativeButton: ButtonInfo?,
) {

  data class ButtonInfo(
    val text: CharSequence,
    val onClick: (() -> Unit)? = null
  )
}