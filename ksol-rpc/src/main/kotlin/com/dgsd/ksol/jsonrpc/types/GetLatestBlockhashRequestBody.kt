package com.dgsd.ksol.jsonrpc.types

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GetLatestBlockhashRequestBody(
  @Json(name = "commitment") val commitment: String,
  @Json(name = "minContextSlot") val minContextSlot: Long?,
)