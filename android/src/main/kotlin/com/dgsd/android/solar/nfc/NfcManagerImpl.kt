package com.dgsd.android.solar.nfc

import android.app.Application
import android.nfc.NfcAdapter

class NfcManagerImpl(application: Application) : NfcManager {

  private val nfcAdapter = NfcAdapter.getDefaultAdapter(application)

  override fun isNfAvailable(): Boolean {
    return nfcAdapter?.isEnabled == true
  }
}