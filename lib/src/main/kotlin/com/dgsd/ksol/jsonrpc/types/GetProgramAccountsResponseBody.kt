package com.dgsd.ksol.jsonrpc.types

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GetProgramAccountsResponseBody(
    @Json(name = "value") val values: List<Value>,
)

@JsonClass(generateAdapter = true)
internal data class Value(
    @Json(name = "account") val account: AccountInfoResponse,
    @Json(name = "pubkey") val pubKey: String,
)