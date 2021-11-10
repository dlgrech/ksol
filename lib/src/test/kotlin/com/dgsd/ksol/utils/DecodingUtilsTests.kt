package com.dgsd.ksol.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DecodingUtilsTests {

    @Test
    fun decodeBase58_returnsExpectedResult() {
        val base58 = "jo91waLQA1NNeBmZKUF"
        val expectedOutput = "this is a test".toByteArray()

        Assertions.assertArrayEquals(expectedOutput, DecodingUtils.decodeBase58(base58))
    }
}