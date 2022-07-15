package com.dgsd.android.solar.common.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

class SystemClipboard(context: Context) {

  private val clipboardManager =
    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

  fun copy(text: CharSequence) {
    clipboardManager.setPrimaryClip(ClipData.newPlainText(text, text))
  }
}