package com.dgsd.ksol.jsonrpc.types

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GetLatestBlockhashResponseBody(
  @Json(name = "value") val value: Value,
) {

  @JsonClass(generateAdapter = true)
  data class Value(
    @Json(name = "blockhash") val blockhash: String,
    @Json(name = "lastValidBlockHeight") val lastValidBlockHeight: Long,
  )
}