package com.dgsd.ksol.keygen

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ChildIndexTests {

    @Test
    fun hardenedChildIndex_whenConstructorCalled_throwsExceptionIfOutOfRange() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            ChildIndex.Hardened(-1)
        }
    }

    @Test
    fun normalChildIndex_whenConstructorCalled_throwsExceptionIfOutOfRange() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            ChildIndex.Hardened(-1)
        }
    }

    @Test
    fun hardenedChildIndex_toBits_setsFirstBitTo1() {
        val index = ChildIndex.Hardened(44)
        Assertions.assertEquals(-2147483604, index.toBits())
    }

    @Test
    fun normalChildIndex_toBits_returnsIndex() {
        val index = ChildIndex.Normal(44)
        Assertions.assertEquals(44, index.toBits())
    }
}