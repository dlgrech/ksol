package com.dgsd.android.solar.deeplink

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri

object SolarDeeplinkingFactory {

  fun createSolPayRequestIntent(context: Context, requestUri: String): Intent {
    return Intent(Intent.ACTION_VIEW, requestUri.toUri()).apply {
      setPackage(context.packageName)
    }
  }

  fun createScanQRIntent(context: Context): Intent {
    return Intent(
      Intent.ACTION_VIEW,
      Uri.Builder()
        .scheme(SolarDeeplinkingConstants.SCHEME)
        .authority(SolarDeeplinkingConstants.DestinationHosts.SCAN_QR)
        .build()
    ).apply {
      setPackage(context.packageName)
    }
  }

  fun createShareAddressIntent(context: Context): Intent {
    return Intent(
      Intent.ACTION_VIEW,
      Uri.Builder()
        .scheme(SolarDeeplinkingConstants.SCHEME)
        .authority(SolarDeeplinkingConstants.DestinationHosts.YOUR_ADDRESS)
        .build()
    ).apply {
      setPackage(context.packageName)
    }
  }
}