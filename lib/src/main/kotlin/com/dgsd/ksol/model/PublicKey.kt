package com.dgsd.ksol.model

import com.dgsd.ksol.utils.DecodingUtils
import com.dgsd.ksol.utils.EncodingUtils

data class PublicKey internal constructor(val key: ByteArray) {

    init {
        require(key.size == PUBLIC_KEY_LENGTH) { "Invalid key" }
    }

    override fun equals(other: Any?): Boolean {
        return if (this === other) {
            true
        } else if (other !is PublicKey) {
            false
        } else {
            this.key.contentEquals(other.key)
        }
    }

    override fun hashCode(): Int {
        return key.contentHashCode()
    }

    override fun toString(): String {
        return toBase58String()
    }

    fun toBase58String(): String {
        return EncodingUtils.encodeBase58(key)
    }

    companion object {

        /**
         * The number of bytes expected to be contained in valid public key
         */
        const val PUBLIC_KEY_LENGTH = 32

        fun fromBase58(hash: String): PublicKey {
            return PublicKey(DecodingUtils.decodeBase58(hash))
        }

        fun fromByteArray(byteArray: ByteArray): PublicKey {
            return PublicKey(byteArray)
        }
    }
}
