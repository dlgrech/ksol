package com.dgsd.ksol.model

import org.bitcoinj.core.Base58

data class PublicKey internal constructor(val key: ByteArray) {

    override fun equals(other: Any?): Boolean {
        return when (other) {
            this -> true
            !is PublicKey -> false
            else -> this.key.contentEquals(other.key)
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

        fun fromBase58(hash: String): PublicKey {
            return PublicKey(Base58.decode(hash))
        }
    }
}
