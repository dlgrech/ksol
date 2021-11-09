package com.dgsd.ksol.utils

import org.bitcoinj.core.Base58

internal object DecodingUtils {

    fun decodeBase58(input: String): ByteArray {
        return Base58.decode(input)
    }
}