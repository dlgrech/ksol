package com.dgsd.ksol.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class EncodingUtilsTests {

    @Test
    fun encodeBase58_returnsExpectedResult() {
        val input = "this is a test".toByteArray()
        val expectedOutput = "jo91waLQA1NNeBmZKUF"

        Assertions.assertEquals(expectedOutput, EncodingUtils.encodeBase58(input))
    }

    @Test
    fun encodeBase64_returnsExpectedResult() {
        val input = "this is a test".toByteArray()
        val expectedOutput = "dGhpcyBpcyBhIHRlc3Q="

        Assertions.assertEquals(expectedOutput, EncodingUtils.encodeBase64(input))
    }
}