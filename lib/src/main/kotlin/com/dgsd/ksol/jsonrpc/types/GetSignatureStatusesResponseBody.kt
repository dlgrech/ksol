package com.dgsd.ksol.jsonrpc.types

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GetSignatureStatusesResponseBody(
    @Json(name = "value") val value: List<Value?>,
) {

    @JsonClass(generateAdapter = true)
    internal data class Value(
        @Json(name = "slot") val slot: Long,
        @Json(name = "err") val error: ErrorResponse?,
        @Json(name = "confirmationStatus") val confirmationStatus: String?
    )
}