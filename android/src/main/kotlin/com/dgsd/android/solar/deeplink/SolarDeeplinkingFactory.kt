package com.dgsd.android.solar.deeplink

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

object SolarDeeplinkingFactory {

  fun createSolPayRequestIntent(context: Context, requestUri: String): Intent {
    return Intent(Intent.ACTION_VIEW, requestUri.toUri()).apply {
      setPackage(context.packageName)
    }
  }
}