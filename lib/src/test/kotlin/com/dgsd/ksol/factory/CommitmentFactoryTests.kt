package com.dgsd.ksol.factory

import com.dgsd.ksol.model.Commitment
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class CommitmentFactoryTests {

    private val commitmentToRpcMapping = mapOf(
        Commitment.FINALIZED to "finalized",
        Commitment.CONFIRMED to "confirmed",
        Commitment.PROCESSED to "processed",
    )

    @TestFactory
    fun testFactory_toRpcValue() = commitmentToRpcMapping.map { (commitment, rpcValue) ->
        DynamicTest.dynamicTest("toRpcValue($commitment) == $rpcValue") {
            Assertions.assertEquals(
                rpcValue,
                CommitmentFactory.toRpcValue(commitment)
            )
        }
    }

    @TestFactory
    fun testFactory_fromRpcValue() = commitmentToRpcMapping.map { (commitment, rpcValue) ->
        DynamicTest.dynamicTest("fromRpcValue($rpcValue) == $commitment") {
            Assertions.assertEquals(
                commitment,
                CommitmentFactory.fromRpcValue(rpcValue)
            )
        }
    }

    @Test
    fun fromRpcValue_withUnknownValue_returnsNull() {
        Assertions.assertNull(CommitmentFactory.fromRpcValue("this is rubbish"))
    }

    @Test
    fun fromRpcValue_withNullValue_returnsNull() {
        Assertions.assertNull(CommitmentFactory.fromRpcValue(null))
    }
}