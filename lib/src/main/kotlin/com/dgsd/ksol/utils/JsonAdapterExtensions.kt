package com.dgsd.ksol.utils

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException

/**
 * Convert the given `text` into an instance of `T`.
 *
 * The given text is expected to be a json representation of `T`.
 *
 * If this is not the case, or if there is a problem decoding the text to `T`, `null` will be returned.
 */
fun <T> JsonAdapter<T>.fromJsonOrNull(text: String): T? {
    return try {
        this.fromJson(text)
    } catch (ex: JsonDataException) {
        null
    }
}