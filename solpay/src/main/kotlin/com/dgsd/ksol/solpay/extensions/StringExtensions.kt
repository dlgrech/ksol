package com.dgsd.ksol.solpay.extensions

import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

internal fun String.urlEncode(): String {
  return URLEncoder.encode(this, StandardCharsets.UTF_8)
    .replace("+", "%20")
    .replace("!", "%21")
    .replace("'", "%27")
    .replace("(", "%28")
    .replace(")", "%29")
    .replace("~", "%7E")
}

internal fun String.urlDecode(): String {
  return URLDecoder.decode(this, StandardCharsets.UTF_8)
}

internal fun String.getPathPortion(): String {
  val withoutScheme = dropWhile { it != ':' }.drop(1)
  return withoutScheme.takeWhile { it != '?' }
}

/**
 * Creates a [Map<String, List<String>>] of the query parameters included in this [URI]
 */
internal fun String.getRawQueryParameters(): Map<String, List<String>> {
  return dropWhile { it != '?' }
    .drop(1)
    .split('&')
    .map { keyAndValue -> keyAndValue.split("=") }
    .filter { it.isNotEmpty() && it.first().isNotEmpty() }
    .groupBy(
      keySelector = { it.first() },
      valueTransform = { it.getOrElse(1) { "" } }
    )
}