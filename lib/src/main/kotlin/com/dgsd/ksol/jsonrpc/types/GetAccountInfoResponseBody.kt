package com.dgsd.ksol.jsonrpc.types

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GetAccountInfoResponseBody(
    @Json(name = "value") val value: Value?,
) {

    @JsonClass(generateAdapter = true)
    internal data class Value(
        @Json(name = "executable") val isExecutable: Boolean,
        @Json(name = "lamports") val lamports: Long,
        @Json(name = "owner") val ownerHash: String,
        @Json(name = "rentEpoch") val rentEpoch: Long,
        @Json(name = "data") val dataAndFormat: List<String>,
    )
}