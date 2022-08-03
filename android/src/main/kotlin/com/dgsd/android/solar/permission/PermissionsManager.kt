package com.dgsd.android.solar.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PermissionsManager(
  private val context: Context
) {

  fun hasCameraPermissions(): Boolean {
    return getPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
  }

  private fun getPermission(permission: String): Int {
    return ContextCompat.checkSelfPermission(context, permission)
  }
}