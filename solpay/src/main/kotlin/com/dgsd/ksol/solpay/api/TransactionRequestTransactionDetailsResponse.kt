package com.dgsd.ksol.solpay.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class TransactionRequestTransactionDetailsResponse(
  @Json(name = "message") val message: String?,
  @Json(name = "transaction") val transactionBase64: String
)