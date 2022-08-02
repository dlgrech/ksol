package com.dgsd.ksol.solpay.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class TransactionRequestTransactionDetailsRequest(
  @Json(name = "account") val account: String,
)