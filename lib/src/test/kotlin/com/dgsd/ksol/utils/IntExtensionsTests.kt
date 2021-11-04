package com.dgsd.ksol.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class IntExtensionsTests {

    private val intToBytes = listOf(
        1 to byteArrayOf(1, 0, 0, 0),
        20 to byteArrayOf(20, 0, 0, 0),
        44 to byteArrayOf(44, 0, 0, 0),
        128 to byteArrayOf(-128, 0, 0, 0),
        129 to byteArrayOf(-127, 0, 0, 0),
        500 to byteArrayOf(-12, 1, 0, 0),
    )

    @TestFactory
    fun testFactory_toByteArray() = intToBytes.map { (input, expected) ->
        DynamicTest.dynamicTest("${input}toByteArray() == ${expected.toList()}") {
            Assertions.assertArrayEquals(
                expected,
                input.toByteArray()
            )
        }
    }

    @TestFactory
    fun testFactory_reverseBytes() = intToBytes.map { (input, expected) ->
        val reversed = expected.reversedArray()
        DynamicTest.dynamicTest("${input}.reverseBytes() == ${reversed.toList()}") {
            Assertions.assertArrayEquals(
                reversed,
                input.reverseBytes().toByteArray()
            )
        }
    }
}