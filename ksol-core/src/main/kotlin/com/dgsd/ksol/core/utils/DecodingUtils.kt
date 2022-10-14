package com.dgsd.ksol.core.utils

import okio.ByteString.Companion.decodeBase64
import org.bitcoinj.core.Base58

object DecodingUtils {

  fun decodeBase58(input: String): ByteArray {
    return Base58.decode(input)
  }

  fun decodeBase64(input: String): ByteArray {
    return checkNotNull(input.decodeBase64()).toByteArray()
  }
}