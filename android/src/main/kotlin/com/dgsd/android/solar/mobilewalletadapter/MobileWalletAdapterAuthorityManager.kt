package com.dgsd.android.solar.mobilewalletadapter

import android.app.Application

class MobileWalletAdapterAuthorityManager(
  private val application: Application,
) {

  private val authorityPrefix = "${application.packageName}:"

  fun isValidAuthority(bytes: ByteArray): Boolean {
    val incoming = bytes.decodeToString()
    return incoming.startsWith(authorityPrefix) && incoming.length > authorityPrefix.length
  }

  fun createAuthority(callingPackage: String): ByteArray {
    return "$authorityPrefix$callingPackage".toByteArray()
  }
}