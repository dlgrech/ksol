package com.dgsd.android.solar.applock.manager

import android.content.SharedPreferences
import androidx.core.content.edit
import com.dgsd.android.solar.common.model.SensitiveString
import java.time.Clock
import java.time.Duration
import java.util.concurrent.TimeUnit

private const val PREF_CODE_APP_LOCK_CODE = "app_lock_code"
private const val PREF_LAST_UNLOCK_TIME = "last_unlock_time"

private val APP_LOCK_TIMEOUT_MS = TimeUnit.MINUTES.toMillis(5)

class AppLockManagerImpl(
  private val sharedPreferences: SharedPreferences,
  private val clock: Clock
) : AppLockManager {

  override fun updateCode(code: SensitiveString) {
    sharedPreferences.edit {
      putString(PREF_CODE_APP_LOCK_CODE, code.sensitiveValue)
      putLong(PREF_LAST_UNLOCK_TIME, clock.millis())
    }
  }

  override fun hasCode(): Boolean {
    return getCode() != null
  }

  override fun attemptUnlock(candidate: SensitiveString): Boolean {
    val match = candidate.sensitiveValue == getCode()
    if (match) {
      unlock()
    }

    return match
  }

  private fun getCode(): String? {
    return sharedPreferences.getString(PREF_CODE_APP_LOCK_CODE, null)
  }

  private fun getLastUnlockTime(): Long? {
    return sharedPreferences.getLong(PREF_LAST_UNLOCK_TIME, -1L).takeIf { it > 0 }
  }

  override fun unlock() {
    sharedPreferences.edit {
      putLong(PREF_LAST_UNLOCK_TIME, clock.millis())
    }
  }

  override fun shouldShowAppLockEntry(): Boolean {
    val lastUnlockTime = getLastUnlockTime()
    return if (lastUnlockTime == null) {
      true
    } else {
      val timeElapsed = clock.millis() - lastUnlockTime
      timeElapsed > APP_LOCK_TIMEOUT_MS
    }
  }
}