package com.dgsd.ksol.utils

import okio.ByteString.Companion.decodeBase64
import org.bitcoinj.core.Base58

internal object DecodingUtils {

  fun decodeBase58(input: String): ByteArray {
    return Base58.decode(input)
  }

  fun decodeBase64(input: String): ByteArray {
    return checkNotNull(input.decodeBase64()).toByteArray()
  }
}