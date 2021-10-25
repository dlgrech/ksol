package com.dgsd.ksol.jsonrpc.types

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class RecentBlockhashResponseBody(
    @Json(name = "value") val value: Value,
) {

    @JsonClass(generateAdapter = true)
    data class Value(
        @Json(name = "blockhash") val blockhash: String,
        @Json(name = "feeCalculator") val feeCalculator: FeeCalculatorResponseBody,
    )
}