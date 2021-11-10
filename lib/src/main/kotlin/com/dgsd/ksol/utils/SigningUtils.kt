package com.dgsd.ksol.utils

import com.dgsd.ksol.model.PrivateKey
import com.iwebpp.crypto.TweetNaclFast

object SigningUtils {

    /**
     * Signs the input data using the given key and returns a signature.
     */
    fun sign(inputData: ByteArray, key: PrivateKey): ByteArray {
        return TweetNaclFast.Signature(
            byteArrayOf(),
            key.key
        ).detached(inputData)
    }
}