package com.dgsd.ksol.utils

/**
 * @see Integer.reverseBytes
 */
fun Int.reverseBytes(): Int {
    return Integer.reverseBytes(this)
}

/**
 * Converts this 32-bit `Int` into a `ByteArray` representing its 4 bytes
 */
fun Int.toByteArray(): ByteArray {
    return byteArrayOf(
        (this shr 0).toByte(),
        (this shr 8).toByte(),
        (this shr 16).toByte(),
        (this shr 24).toByte(),
    )
}