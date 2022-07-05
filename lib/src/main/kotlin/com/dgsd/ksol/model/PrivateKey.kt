package com.dgsd.ksol.model

import com.dgsd.ksol.utils.DecodingUtils
import com.dgsd.ksol.utils.EncodingUtils

data class PrivateKey internal constructor(val key: ByteArray) {

    override fun equals(other: Any?): Boolean {
        return if (this === other) {
            true
        } else if (other !is PrivateKey) {
            false
        } else {
            this.key.contentEquals(other.key)
        }
    }

    override fun hashCode(): Int {
        return key.contentHashCode()
    }

    override fun toString(): String {
        return "[redacted]"
    }

    fun toBase58String(): String {
        return EncodingUtils.encodeBase58(key)
    }

    companion object {

        fun fromBase58(hash: String): PrivateKey {
            return PrivateKey(DecodingUtils.decodeBase58(hash))
        }
    }
}
