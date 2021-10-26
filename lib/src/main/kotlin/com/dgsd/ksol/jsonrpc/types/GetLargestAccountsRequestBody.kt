package com.dgsd.ksol.jsonrpc.types

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GetLargestAccountsRequestBody(
    @Json(name = "commitment") val commitment: String,
    @Json(name = "filter") val filter: String?
) {

    companion object {
        val FILTER_CIRCULATING = "circulating"
        val FILTER_NON_CIRCULATING = "nonCirculating"
    }
}