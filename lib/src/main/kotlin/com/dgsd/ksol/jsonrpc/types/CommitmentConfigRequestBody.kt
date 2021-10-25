package com.dgsd.ksol.jsonrpc.types

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class CommitmentConfigRequestBody(
    @Json(name = "commitment") val commitment: String,
) {

    companion object {
        val FINALIZED = "finalized"
        val CONFIRMED = "confirmed"
        val PROCESSED = "processed"
    }
}