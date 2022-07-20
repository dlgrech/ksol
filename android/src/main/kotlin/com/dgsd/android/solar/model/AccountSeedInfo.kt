package com.dgsd.android.solar.model

import com.dgsd.android.solar.common.model.SensitiveList
import com.dgsd.android.solar.common.model.SensitiveString

data class AccountSeedInfo(
  val seedPhrase: SensitiveList<String>,
  val passPhrase: SensitiveString
)