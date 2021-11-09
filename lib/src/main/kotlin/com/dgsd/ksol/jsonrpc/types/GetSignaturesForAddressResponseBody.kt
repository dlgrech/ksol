package com.dgsd.ksol.jsonrpc.types

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GetSignaturesForAddressResponseBody(
    @Json(name = "signature") val signature: String,
    @Json(name = "slot") val slot: Long,
    @Json(name = "blockTime") val blockTime: Long?,
    @Json(name = "memo") val memo: String?,
    @Json(name = "err") val error: ErrorResponse?,
)