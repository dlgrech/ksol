package com.dgsd.ksol

import com.dgsd.ksol.model.PublicKey
import com.dgsd.ksol.collections.BidirectionalMap

/**
 * Keeps track of the mappings between:
 *
 * ```
 * PublicKey <-> RpcRequestId <-> SubscriptionId
 * ```
 */
internal class SubscriptionRegistry {

    private val accountKeyToRpcRequestIdMap = BidirectionalMap<PublicKey, String>()
    private val rpcRequestIdToSubscriptionId = BidirectionalMap<String, Long>()

    /**
     * Called when a WebSocket Subscription `accountSubscribe` request (with an id of `rpcRequestId`) is made for
     * the given account
     */
    fun onAccountSubscribe(
        rpcRequestId: String,
        accountKey: PublicKey,
    ) {
        accountKeyToRpcRequestIdMap[accountKey] = rpcRequestId
    }

    /**
     * Called when a WebSocket message is received from the server, indicating that a subscription has been confirmed.
     */
    fun onSubscriptionConfirmation(rpcRequestId: String, subscriptionId: Long) {
        rpcRequestIdToSubscriptionId[rpcRequestId] = subscriptionId
    }

    /**
     * Returns the `Subscription ID` for a previously confirmed `accountSubscribe` request for `accountKey`
     */
    fun getSubscriptionIdFromAccount(accountKey: PublicKey): Long? {
        return accountKeyToRpcRequestIdMap.getFromKey(accountKey)?.let { rpcRequestId ->
            rpcRequestIdToSubscriptionId.getFromKey(rpcRequestId)
        }
    }

    /**
     * Returns the account key for a previously confirmed `accountSubscribe` request represented by the
     * given `subscriptionId`
     */
    fun getAccountKeyFromSubscriptionId(subscriptionId: Long): PublicKey? {
        return rpcRequestIdToSubscriptionId.getFromValue(subscriptionId)?.let { rpcRequestId ->
            accountKeyToRpcRequestIdMap.getFromValue(rpcRequestId)
        }
    }

    /**
     * Removes any registered `accountSubscribe` attempt for the given `accountKey`
     */
    fun clearAccountSubscription(accountKey: PublicKey) {
        val rpcRequestId = accountKeyToRpcRequestIdMap.removeKey(accountKey)
        if (rpcRequestId != null) {
            rpcRequestIdToSubscriptionId.removeKey(rpcRequestId)
        }
    }

    /**
     * Clears all entries from the registry
     */
    fun clear() {
        accountKeyToRpcRequestIdMap.clear()
        rpcRequestIdToSubscriptionId.clear()
    }
}