package com.dgsd.android.solar.applock.manager

import android.content.SharedPreferences
import androidx.core.content.edit
import com.dgsd.android.solar.common.model.SensitiveString

private const val PREF_CODE_APP_LOCK_CODE = "app_lock_code"

class AppLockManagerImpl(
  private val sharedPreferences: SharedPreferences,
) : AppLockManager {

  override fun updateCode(code: SensitiveString) {
    sharedPreferences.edit {
      putString(PREF_CODE_APP_LOCK_CODE, code.sensitiveValue)
    }
  }

  override fun hasCode(): Boolean {
    return getCode() != null
  }

  override fun matches(candidate: String): Boolean {
    return candidate == getCode()
  }

  private fun getCode(): String? {
    return sharedPreferences.getString(PREF_CODE_APP_LOCK_CODE, null)
  }
}