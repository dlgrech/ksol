package com.dgsd.android.solar.applock.manager

import com.dgsd.android.solar.common.model.SensitiveString

interface AppLockManager {

  fun updateCode(code: SensitiveString)

  fun hasCode(): Boolean

  fun matches(candidate: String): Boolean
}