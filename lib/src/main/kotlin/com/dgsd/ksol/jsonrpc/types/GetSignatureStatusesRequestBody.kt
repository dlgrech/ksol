package com.dgsd.ksol.jsonrpc.types

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GetSignatureStatusesRequestBody(
    @Json(name = "searchTransactionHistory") val searchTransactionHistory: Boolean,
)