package com.dgsd.ksol.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class PublicKeyTests {

    @Test
    fun constructor_whenCalledWithSmallByteArray_throwsException() {
        Assertions.assertThrows(
            IllegalArgumentException::class.java,
        ) {
            PublicKey(
                (0 until PublicKey.PUBLIC_KEY_LENGTH - 1)
                    .toList()
                    .toIntArray()
                    .map { it.toByte() }
                    .toByteArray()
            )
        }
    }

    @Test
    fun constructor_whenCalledWithLargeByteArray_throwsException() {
        Assertions.assertThrows(
            IllegalArgumentException::class.java,
        ) {
            PublicKey(
                (0 until PublicKey.PUBLIC_KEY_LENGTH + 1)
                    .toList()
                    .toIntArray()
                    .map { it.toByte() }
                    .toByteArray()
            )
        }
    }

    @Test
    fun equals_whenPassedSameInstance_returnsTrue() {
        val first = PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e")

        Assertions.assertEquals(first, first)
    }

    @Test
    fun equals_whenPassedDifferentInstanceWithSameContent_returnsTrue() {
        val first = PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e")
        val second = PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e")

        Assertions.assertEquals(first, second)
    }

    @Test
    fun equals_whenPassedDifferentContent_returnsFalse() {
        val first = PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e")
        val second =
            PublicKey.fromBase58("4Rf9mGD7FeYknun5JczX5nGLTfQuS1GRjNVfkEMKE92b")

        Assertions.assertNotEquals(first, second)
    }

    @Test
    fun equals_whenPassedDifferentObject_returnsFalse() {
        val first = PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e")
        val second = "a string"

        Assertions.assertNotEquals(first, second)
    }

    @Test
    fun toBase58String_returnsExpectedResult() {
        val base58 = "HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"

        val key = PublicKey.fromBase58(base58)

        Assertions.assertEquals(key.toBase58String(), base58)
    }
}