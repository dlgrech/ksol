package com.dgsd.ksol.solpay.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class TransactionRequestGetDetailsResponse(
  @Json(name = "label") val label: String?,
  @Json(name = "icon") val iconUrl: String?
)