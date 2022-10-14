package com.dgsd.android.solar.model

import com.dgsd.ksol.core.model.Lamports
import java.time.OffsetDateTime

data class LamportsWithTimestamp(
  val lamports: Lamports,
  val timestamp: OffsetDateTime,
)