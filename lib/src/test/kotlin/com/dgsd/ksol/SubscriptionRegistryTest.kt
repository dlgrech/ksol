package com.dgsd.ksol

import com.dgsd.ksol.model.PublicKey
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class SubscriptionRegistryTest {

    @Test
    fun testSuccessfulFlow() {
        val key = PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e")
        val rpcRequestId = "abc123"
        val subscriptionId = 1337L

        val registry = SubscriptionRegistry()

        registry.onAccountSubscribe(rpcRequestId, key)
        registry.onSubscriptionConfirmation(rpcRequestId, subscriptionId)

        Assertions.assertEquals(subscriptionId, registry.getSubscriptionIdFromAccount(key))
        Assertions.assertEquals(key, registry.getAccountKeyFromSubscriptionId(subscriptionId))
    }

    @Test
    fun testClearAccountSubscription_removesAccount() {
        val key = PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e")
        val rpcRequestId = "abc123"
        val subscriptionId = 1337L

        val registry = SubscriptionRegistry()

        registry.onAccountSubscribe(rpcRequestId, key)
        registry.onSubscriptionConfirmation(rpcRequestId, subscriptionId)

        registry.clearAccountSubscription(key)

        Assertions.assertNull(registry.getSubscriptionIdFromAccount(key))
        Assertions.assertNull(registry.getAccountKeyFromSubscriptionId(subscriptionId))
    }

    @Test
    fun testClear_removesAccount() {
        val key = PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e")
        val rpcRequestId = "abc123"
        val subscriptionId = 1337L

        val registry = SubscriptionRegistry()

        registry.onAccountSubscribe(rpcRequestId, key)
        registry.onSubscriptionConfirmation(rpcRequestId, subscriptionId)

        registry.clear()

        Assertions.assertNull(registry.getSubscriptionIdFromAccount(key))
        Assertions.assertNull(registry.getAccountKeyFromSubscriptionId(subscriptionId))
    }

    @Test
    fun getSubscriptionIdFromAccount_forUnknownKey_returnsNull() {
        val registry = SubscriptionRegistry()
        Assertions.assertNull(
            registry.getSubscriptionIdFromAccount(PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"))
        )
    }

    @Test
    fun getAccountKeyFromSubscriptionId_forSubscriptId_returnsNull() {
        val registry = SubscriptionRegistry()
        Assertions.assertNull(
            registry.getAccountKeyFromSubscriptionId(123L)
        )
    }
}