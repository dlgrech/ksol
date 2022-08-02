package com.dgsd.android.solar.common.util

import android.content.Intent
import android.net.Uri

object IntentFactory {

  fun createShareImageIntent(uri: Uri): Intent {
    return Intent(Intent.ACTION_SEND).apply {
      type = "image/png"
      putExtra(Intent.EXTRA_STREAM, uri)
      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
  }
}