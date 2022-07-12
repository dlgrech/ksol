package com.dgsd.ksol.jsonrpc.types

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GetTransactionResponseBody(
    @Json(name = "slot") val slot: Long,
    @Json(name = "blockTime") val blockTime: Long?,
    @Json(name = "transaction") val transaction: TransactionResponse?,
    @Json(name = "meta") val meta: TransactionMetaResponse?,
)