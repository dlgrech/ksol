package com.dgsd.ksol.utils

import okio.ByteString.Companion.toByteString
import org.bitcoinj.core.Base58

internal object EncodingUtils {

    fun encodeBase58(input: ByteArray): String {
        return Base58.encode(input)
    }

    fun encodeBase64(input: ByteArray): String {
        return input.toByteString().base64()
    }
}