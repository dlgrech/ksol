package com.dgsd.ksol

import com.dgsd.ksol.model.Commitment
import com.dgsd.ksol.model.PublicKey
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class SubscriptionRegistryTest {

    @Test
    fun testSuccessfulAccountSubscribeFlow() {
        val key = PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e")
        val rpcRequestId = "abc123"
        val subscriptionId = 1337L

        val registry = SubscriptionRegistry()

        registry.onAccountSubscribe(rpcRequestId, key, Commitment.CONFIRMED)
        registry.onSubscriptionConfirmation(rpcRequestId, subscriptionId)

        Assertions.assertEquals(subscriptionId, registry.getSubscriptionIdFromAccount(key))
        Assertions.assertEquals(key, registry.getAccountKeyFromSubscriptionId(subscriptionId))
        Assertions.assertEquals(Commitment.CONFIRMED, registry.getCommitmentFromSubscriptionId(subscriptionId))
    }

    @Test
    fun testSuccessfulSignatureSubscribeFlow() {
        val signature = "4nofvj5RZ8VjjBnEF1TDRpLbcYB694WCsfLqn4KppPQSpLUcqGHnYzcCn6QW2J5MrHtd2fsCpX27umtnvFQxcRiU"
        val rpcRequestId = "abc123"
        val subscriptionId = 1337L

        val registry = SubscriptionRegistry()

        registry.onSignatureSubscribe(rpcRequestId, signature, Commitment.CONFIRMED)
        registry.onSubscriptionConfirmation(rpcRequestId, subscriptionId)

        Assertions.assertEquals(subscriptionId, registry.getSubscriptionIdFromSignature(signature))
        Assertions.assertEquals(signature, registry.getSignatureFromSubscriptionId(subscriptionId))
        Assertions.assertEquals(Commitment.CONFIRMED, registry.getCommitmentFromSubscriptionId(subscriptionId))
    }

    @Test
    fun testClearAccountSubscription_removesAccount() {
        val key = PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e")
        val rpcRequestId = "abc123"
        val subscriptionId = 1337L

        val registry = SubscriptionRegistry()

        registry.onAccountSubscribe(rpcRequestId, key, Commitment.CONFIRMED)
        registry.onSubscriptionConfirmation(rpcRequestId, subscriptionId)

        registry.clearAccountSubscription(key)

        Assertions.assertNull(registry.getSubscriptionIdFromAccount(key))
        Assertions.assertNull(registry.getAccountKeyFromSubscriptionId(subscriptionId))
        Assertions.assertNull(registry.getCommitmentFromSubscriptionId(subscriptionId))
    }

    @Test
    fun testClearTransactionSignatureSubscription_removesSignature() {
        val signature = "4nofvj5RZ8VjjBnEF1TDRpLbcYB694WCsfLqn4KppPQSpLUcqGHnYzcCn6QW2J5MrHtd2fsCpX27umtnvFQxcRiU"
        val rpcRequestId = "abc123"
        val subscriptionId = 1337L

        val registry = SubscriptionRegistry()

        registry.onSignatureSubscribe(rpcRequestId, signature, Commitment.CONFIRMED)
        registry.onSubscriptionConfirmation(rpcRequestId, subscriptionId)

        registry.clearSignatureSubscription(signature)

        Assertions.assertNull(registry.getSubscriptionIdFromSignature(signature))
        Assertions.assertNull(registry.getSignatureFromSubscriptionId(subscriptionId))
        Assertions.assertNull(registry.getCommitmentFromSubscriptionId(subscriptionId))

    }

    @Test
    fun testClear_removesAccount() {
        val key = PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e")
        val rpcRequestId = "abc123"
        val subscriptionId = 1337L

        val registry = SubscriptionRegistry()

        registry.onAccountSubscribe(rpcRequestId, key, Commitment.CONFIRMED)
        registry.onSubscriptionConfirmation(rpcRequestId, subscriptionId)

        registry.clear()

        Assertions.assertNull(registry.getSubscriptionIdFromAccount(key))
        Assertions.assertNull(registry.getAccountKeyFromSubscriptionId(subscriptionId))
        Assertions.assertNull(registry.getCommitmentFromSubscriptionId(subscriptionId))
    }


    @Test
    fun testClear_removesSignature() {
        val signature = "4nofvj5RZ8VjjBnEF1TDRpLbcYB694WCsfLqn4KppPQSpLUcqGHnYzcCn6QW2J5MrHtd2fsCpX27umtnvFQxcRiU"
        val rpcRequestId = "abc123"
        val subscriptionId = 1337L

        val registry = SubscriptionRegistry()

        registry.onSignatureSubscribe(rpcRequestId, signature, Commitment.CONFIRMED)
        registry.onSubscriptionConfirmation(rpcRequestId, subscriptionId)

        registry.clear()

        Assertions.assertNull(registry.getSubscriptionIdFromSignature(signature))
        Assertions.assertNull(registry.getSignatureFromSubscriptionId(subscriptionId))
        Assertions.assertNull(registry.getCommitmentFromSubscriptionId(subscriptionId))
    }

    @Test
    fun getSubscriptionIdFromAccount_forUnknownKey_returnsNull() {
        val registry = SubscriptionRegistry()
        Assertions.assertNull(
            registry.getSubscriptionIdFromAccount(PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"))
        )
    }

    @Test
    fun getAccountKeyFromSubscriptionId_forSubscriptionId_returnsNull() {
        val registry = SubscriptionRegistry()
        Assertions.assertNull(
            registry.getAccountKeyFromSubscriptionId(123L)
        )
    }


    @Test
    fun getSubscriptionIdFromSignature_forUnknownSignature_returnsNull() {
        val registry = SubscriptionRegistry()
        Assertions.assertNull(
            registry.getSubscriptionIdFromSignature(
                "4nofvj5RZ8VjjBnEF1TDRpLbcYB694WCsfLqn4KppPQSpLUcqGHnYzcCn6QW2J5MrHtd2fsCpX27umtnvFQxcRiU"
            )
        )
    }

    @Test
    fun getSignatureFromSubscriptionId_forSubscriptionId_returnsNull() {
        val registry = SubscriptionRegistry()
        Assertions.assertNull(
            registry.getSignatureFromSubscriptionId(123L)
        )
    }
}