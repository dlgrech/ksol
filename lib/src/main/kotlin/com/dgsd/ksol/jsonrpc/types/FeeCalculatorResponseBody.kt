package com.dgsd.ksol.jsonrpc.types

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class FeeCalculatorResponseBody(
    @Json(name = "lamportsPerSignature") val value: Long,
)