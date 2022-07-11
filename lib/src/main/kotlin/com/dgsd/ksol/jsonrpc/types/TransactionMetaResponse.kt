package com.dgsd.ksol.jsonrpc.types

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


/**
 * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api#transaction-structure">json-rpc API</a>
 */
@JsonClass(generateAdapter = true)
internal data class TransactionMetaResponse(

    /**
     * Fee this transaction was charged,
     */
    @Json(name = "fee") val fee: Long,

    /**
     * Account balances before the transaction was processed
     */
    @Json(name = "preBalances") val preBalances: List<Long>,

    /**
     * Account balances after the transaction was processed
     */
    @Json(name = "postBalances") val postBalances: List<Long>,

    /**
     * Array of string log messages or null if log message recording was not enabled during this transaction
     */
    @Json(name = "logMessages") val logMessages: List<String>,
)
