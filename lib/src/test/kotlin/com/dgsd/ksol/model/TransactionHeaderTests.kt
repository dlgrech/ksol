package com.dgsd.ksol.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

private val PUBLIC_KEY = PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e")

class TransactionHeaderTests {

    private val input = mapOf(
        listOf(
            TransactionAccountMetadata(PUBLIC_KEY, isSigner = true, isFeePayer = true, isWritable = false)
        ) to TransactionHeader(1, 1, 0),

        listOf(
            TransactionAccountMetadata(PUBLIC_KEY, isSigner = true, isFeePayer = true, isWritable = false),
            TransactionAccountMetadata(PUBLIC_KEY, isSigner = true, isFeePayer = true, isWritable = false)
        ) to TransactionHeader(2, 2, 0),

        listOf(
            TransactionAccountMetadata(PUBLIC_KEY, isSigner = true, isFeePayer = true, isWritable = true),
            TransactionAccountMetadata(PUBLIC_KEY, isSigner = true, isFeePayer = true, isWritable = false)
        ) to TransactionHeader(2, 1, 0),

        listOf(
            TransactionAccountMetadata(PUBLIC_KEY, isSigner = true, isFeePayer = true, isWritable = true),
            TransactionAccountMetadata(PUBLIC_KEY, isSigner = false, isFeePayer = false, isWritable = true),
            TransactionAccountMetadata(PUBLIC_KEY, isSigner = false, isFeePayer = false, isWritable = false)
        ) to TransactionHeader(1, 0, 1),
    )

    @TestFactory
    fun testFactory_createFrom() = input.entries.mapIndexed() { index, (input, expected) ->
        DynamicTest.dynamicTest(
            "createFrom_case$index"
        ) {
            Assertions.assertEquals(
                expected,
                TransactionHeader.createFrom(input)
            )
        }
    }
}