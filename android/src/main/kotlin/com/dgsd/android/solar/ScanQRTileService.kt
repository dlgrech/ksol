package com.dgsd.android.solar

import android.content.Intent
import android.service.quicksettings.TileService
import com.dgsd.android.solar.deeplink.SolarDeeplinkingFactory

class ScanQRTileService : TileService() {

  override fun onClick() {
    startActivityAndCollapse(
      SolarDeeplinkingFactory.createScanQRIntent(this).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
    )
  }
}