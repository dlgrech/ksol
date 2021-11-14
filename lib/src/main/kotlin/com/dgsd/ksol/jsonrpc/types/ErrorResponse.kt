package com.dgsd.ksol.jsonrpc.types

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ErrorResponse(
    @Json(name = "code") val code: Int,
    @Json(name = "message") val message: String,
)