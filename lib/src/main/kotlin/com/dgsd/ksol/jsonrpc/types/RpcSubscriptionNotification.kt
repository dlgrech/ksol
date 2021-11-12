package com.dgsd.ksol.jsonrpc.types

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class RpcSubscriptionNotification<T>(
    @Json(name = "jsonrpc") val jsonRpc: String,
    @Json(name = "method") val method: String,
    @Json(name = "params") val params: Params<T>,
) {
    @JsonClass(generateAdapter = true)
    internal data class Params<T>(
        @Json(name = "subscription") val subscriptionId: Long,
        @Json(name = "result") val result: Result<T>,
    ) {
        @JsonClass(generateAdapter = true)
        internal data class Result<T>(
            @Json(name = "context") val context: Context,
            @Json(name = "value") val value: T,
        ) {
            @JsonClass(generateAdapter = true)
            internal data class Context(
                @Json(name = "slot") val slot: Long,
            )
        }
    }
}