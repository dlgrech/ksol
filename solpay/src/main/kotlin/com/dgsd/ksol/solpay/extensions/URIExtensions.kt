package com.dgsd.ksol.solpay.extensions

import java.net.URI

internal fun URI.getPathName(): String {
  val withoutScheme = this.schemeSpecificPart
  return withoutScheme.takeWhile { it != '?' }
}

/**
 * Creates a [Map<String, List<String>>] of the query parameters included in this [URI]
 */
internal fun URI.getRawQueryParameters(): Map<String, List<String>> {
  return this.toString()
    .dropWhile { it != '?' }
    .drop(1)
    .split('&')
    .map { keyAndValue -> keyAndValue.split("=") }
    .filter { it.isNotEmpty() && it.first().isNotEmpty() }
    .groupBy(
      keySelector = { it.first() },
      valueTransform = { it.getOrElse(1) { "" } }
    )
}