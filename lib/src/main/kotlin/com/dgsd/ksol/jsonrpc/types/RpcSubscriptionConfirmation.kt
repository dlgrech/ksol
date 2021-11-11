package com.dgsd.ksol.jsonrpc.types

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class RpcSubscriptionConfirmation(
    @Json(name = "id") val id: String,
    @Json(name = "jsonrpc") val jsonRpc: String,
    @Json(name = "result") val result: Long,
)