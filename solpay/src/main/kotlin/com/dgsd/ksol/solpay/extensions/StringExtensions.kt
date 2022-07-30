package com.dgsd.ksol.solpay.extensions

import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

fun String.urlEncode(): String {
  return URI(null, null, this, null).toASCIIString()
}

fun String.urlDecode(): String {
  return URLDecoder.decode(this, StandardCharsets.UTF_8)
}