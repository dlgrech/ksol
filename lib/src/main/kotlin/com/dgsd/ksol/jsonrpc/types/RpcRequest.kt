package com.dgsd.ksol.jsonrpc.types

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class RpcRequest(
    @Json(name = "id") val id: String,
    @Json(name = "jsonrpc") val jsonRpc: String,
    @Json(name = "method") val methodName: String,
    @Json(name = "params") val params: List<Any>,
)