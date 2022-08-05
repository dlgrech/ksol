package com.dgsd.android.solar.applock.manager

import com.dgsd.android.solar.common.model.SensitiveString

interface AppLockManager {

  fun shouldShowAppLockEntry(): Boolean

  fun updateCode(code: SensitiveString)

  fun hasCode(): Boolean

  fun attemptUnlock(candidate: SensitiveString): Boolean

  fun unlock()
}