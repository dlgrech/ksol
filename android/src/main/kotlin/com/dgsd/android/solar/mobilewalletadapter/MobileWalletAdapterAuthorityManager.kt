package com.dgsd.android.solar.mobilewalletadapter

import android.app.Application

class MobileWalletAdapterAuthorityManager(
  private val application: Application,
) {

  private val delimiter = ":"

  private val authorityPrefix = application.packageName

  fun isValidAuthority(callingPackage: String?, bytes: ByteArray): Boolean {
    val segments = bytes.decodeToString().split(delimiter)
    return if (callingPackage == null) {
      false
    } else if (segments.size != 3) {
      false
    } else if (segments.first() != authorityPrefix) {
      false
    } else if (callingPackage != segments[1]) {
      false
    } else {
      val callingPackageUid = getPackageUid(callingPackage)
      callingPackageUid != NO_UID && callingPackageUid == segments[2].toIntOrNull()
    }
  }

  fun createAuthority(callingPackage: String): ByteArray {
    return buildString {
      append(authorityPrefix)
      append(delimiter)
      append(callingPackage)
      append(delimiter)
      append(getPackageUid(callingPackage))
    }.toByteArray()
  }

  private fun getPackageUid(packageName: String): Int {
    return runCatching {
      application.packageManager.getPackageUid(packageName, 0)
    }.getOrDefault(NO_UID)
  }

  companion object {
    private const val NO_UID = -1
  }
}