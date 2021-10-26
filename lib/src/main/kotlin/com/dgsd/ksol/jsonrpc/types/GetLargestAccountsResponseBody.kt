package com.dgsd.ksol.jsonrpc.types

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GetLargestAccountsResponseBody(
    @Json(name = "value") val value: List<AddressAndBalance>,
) {

    @JsonClass(generateAdapter = true)
    internal data class AddressAndBalance(
        @Json(name = "address") val address: String,
        @Json(name = "lamports") val lamports: Long,
    )
}