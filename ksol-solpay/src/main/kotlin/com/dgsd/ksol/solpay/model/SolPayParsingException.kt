package com.dgsd.ksol.solpay.model

/**
 * Thrown when a given URL could not be parsed as a valid SolPay input
 */
class SolPayParsingException internal constructor(
  url: String,
  reason: String,
  cause: Throwable? = null
) : RuntimeException("Error parsing SolPay URL: $url ($reason)", cause) {

  internal constructor(url: String, cause: Throwable) : this(url, cause.message.orEmpty(), cause)
}