package com.dgsd.ksol.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class PrivateKeyTests {

    @Test
    fun equals_whenPassedSameInstance_returnsTrue() {
        val first = PrivateKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e")

        Assertions.assertEquals(first, first)
    }

    @Test
    fun equals_whenPassedDifferentInstanceWithSameContent_returnsTrue() {
        val first = PrivateKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e")
        val second = PrivateKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e")

        Assertions.assertEquals(first, second)
    }

    @Test
    fun equals_whenPassedDifferentContent_returnsFalse() {
        val first = PrivateKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e")
        val second =
            PrivateKey.fromBase58("5ysPKzei6U5b1KTRs7XjwUL7335E8L1eta531oQkXP63Wf1jkavEyov1zyNX928hHhNkfEpVptACSfWPZtbzgeoa")

        Assertions.assertNotEquals(first, second)
    }

    @Test
    fun equals_whenPassedDifferentObject_returnsFalse() {
        val first = PrivateKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e")
        val second = "a string"

        Assertions.assertNotEquals(first, second)
    }

    @Test
    fun toBase58String_returnsExpectedResult() {
        val base58 = "HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"

        val key = PrivateKey.fromBase58(base58)

        Assertions.assertEquals(key.toBase58String(), base58)
    }

    @Test
    fun toString_doesNotReturnPrivateKey() {
        val base58 = "HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"

        val key = PrivateKey.fromBase58(base58)

        Assertions.assertNotEquals(key.toString(), base58)
    }
    
}