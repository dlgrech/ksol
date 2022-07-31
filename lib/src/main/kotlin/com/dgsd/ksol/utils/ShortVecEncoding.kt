package com.dgsd.ksol.utils

import org.bitcoinj.core.Utils
import java.nio.ByteBuffer
import kotlin.experimental.and

/**
 * @see https://github.com/solana-labs/solana/blob/master/sdk/program/src/short_vec.rs
 */
internal object ShortVecEncoding {

    /**
     * Return the serialized length in compact-short format.
     */
    fun encodeLength(input: Int): ByteArray {
        val output = ByteArray(10)
        var prevInput = input
        var cursor = 0

        while (true) {
            var elem = prevInput and 0x7f
            prevInput = prevInput shr 7

            if (prevInput == 0) {
                Utils.uint16ToByteArrayLE(elem, output, cursor)
                break
            } else {
                elem = elem or 0x80
                Utils.uint16ToByteArrayLE(elem, output, cursor)
                cursor += 1
            }
        }

        val bytes = ByteArray(cursor + 1)
        System.arraycopy(output, 0, bytes, 0, cursor + 1)

        return bytes
    }

    fun decodeLength(input: ByteBuffer): Int {
        var len = 0
        var size = 0
        while (input.hasRemaining()) {
            val elem = input.get()
            len = len or ((elem and 0x7f).toInt() shl (size * 7))
            size++

            if((elem and 0x80.toByte()).toInt() == 0) {
                break
            }
        }

        return len
    }
}