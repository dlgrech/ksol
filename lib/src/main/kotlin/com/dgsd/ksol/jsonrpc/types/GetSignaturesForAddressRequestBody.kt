package com.dgsd.ksol.jsonrpc.types

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GetSignaturesForAddressRequestBody(
    @Json(name = "commitment") val commitment: String,
    @Json(name = "limit") val limit: Int,
    @Json(name = "before") val beforeTransactionSignature: String?,
    @Json(name = "until") val untilTransactionSignature: String?,
)