package com.dgsd.android.solar

import android.content.Intent
import android.service.quicksettings.TileService
import com.dgsd.android.solar.deeplink.SolarDeeplinkingFactory

class ShareAddressTileService : TileService() {

  override fun onClick() {
    startActivityAndCollapse(
      SolarDeeplinkingFactory.createShareAddressIntent(this).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
    )
  }
}