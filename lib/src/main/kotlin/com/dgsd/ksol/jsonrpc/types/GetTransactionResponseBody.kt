package com.dgsd.ksol.jsonrpc.types

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GetTransactionResponseBody(
    @Json(name = "transaction") val transaction: TransactionResponse?,
    @Json(name = "meta") val meta: TransactionMetaResponse?,
)