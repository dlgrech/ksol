package com.dgsd.ksol.jsonrpc.types

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GetSupplyResponseBody(
    @Json(name = "value") val value: Value,
) {


    @JsonClass(generateAdapter = true)
    internal data class Value(
        @Json(name = "circulating") val circulating: Long,
        @Json(name = "nonCirculating") val nonCirculating: Long,
        @Json(name = "total") val total: Long,
    )
}