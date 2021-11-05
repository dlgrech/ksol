package com.dgsd.ksol.jsonrpc.types

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class RpcResponse<T>(
    @Json(name = "id") val id: String?,
    @Json(name = "jsonrpc") val jsonRpc: String,
    @Json(name = "result") val result: T?,
    @Json(name = "error") val error: Error?,
) {

    @JsonClass(generateAdapter = true)
    internal data class Error(
        @Json(name = "code") val code: Int,
        @Json(name = "message") val message: String,
    )
}