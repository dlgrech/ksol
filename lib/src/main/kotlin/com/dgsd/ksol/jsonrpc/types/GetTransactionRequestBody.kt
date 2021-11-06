package com.dgsd.ksol.jsonrpc.types

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GetTransactionRequestBody(
    @Json(name = "commitment") val commitment: String,
    @Json(name = "encoding") val encoding: String
)