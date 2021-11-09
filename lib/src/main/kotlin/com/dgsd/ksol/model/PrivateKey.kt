package com.dgsd.ksol.model

import org.bitcoinj.core.Base58

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
        return toBase58String()
    }

    fun toBase58String(): String {
        return Base58.encode(key)
    }

    companion object {

        fun fromBase58(hash: String): PrivateKey {
            return PrivateKey(Base58.decode(hash))
        }
    }
}
