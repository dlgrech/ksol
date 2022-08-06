package com.dgsd.ksol

import com.dgsd.ksol.collections.BidirectionalMap
import com.dgsd.ksol.model.Commitment
import com.dgsd.ksol.model.PublicKey
import com.dgsd.ksol.model.TransactionSignature

/**
 * Keeps track of the mappings between:
 *
 * ```
 * Key <-> RpcRequestId <-> SubscriptionId
 * ```
 */
internal class SubscriptionRegistry {

    private val accountKeyToRpcRequestIdMap = BidirectionalMap<PublicKey, String>()
    private val transactionSignatureToRpcRequestIdMap = BidirectionalMap<TransactionSignature, String>()
    private val rpcRequestIdToSubscriptionId = BidirectionalMap<String, Long>()
    private val rpcRequestIdToCommitment = mutableMapOf<String, Commitment>()

    /**
     * Called when a WebSocket Subscription `accountSubscribe` request (with an id of `rpcRequestId`) is made for
     * the given account
     */
    fun onAccountSubscribe(
        rpcRequestId: String,
        accountKey: PublicKey,
        commitment: Commitment,
    ) {
        accountKeyToRpcRequestIdMap[accountKey] = rpcRequestId
        rpcRequestIdToCommitment[rpcRequestId] = commitment
    }

    /**
     * Called when a WebSocket Subscription `signatureSubscribe` request (with an id of `rpcRequestId`) is made for
     * the given transaction signature
     */
    fun onSignatureSubscribe(
        rpcRequestId: String,
        transactionSignature: TransactionSignature,
        commitment: Commitment,
    ) {
        transactionSignatureToRpcRequestIdMap[transactionSignature] = rpcRequestId
        rpcRequestIdToCommitment[rpcRequestId] = commitment
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
     * Returns the `Subscription ID` for a previously confirmed `signatureSubscribe` request for `transactionSignature`
     */
    fun getSubscriptionIdFromSignature(transactionSignature: TransactionSignature): Long? {
        return transactionSignatureToRpcRequestIdMap.getFromKey(transactionSignature)?.let { rpcRequestId ->
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
     * Returns the transaction signature for a previously confirmed `signatureSubscribe` request represented by the
     * given `subscriptionId`
     */
    fun getSignatureFromSubscriptionId(subscriptionId: Long): TransactionSignature? {
        return rpcRequestIdToSubscriptionId.getFromValue(subscriptionId)?.let { rpcRequestId ->
            transactionSignatureToRpcRequestIdMap.getFromValue(rpcRequestId)
        }
    }

    /**
     * Returns the [Commitment] that was used when first subscribing to a subscription represented by the
     * given `subscriptionId
     */
    fun getCommitmentFromSubscriptionId(subscriptionId: Long): Commitment? {
        return rpcRequestIdToSubscriptionId.getFromValue(subscriptionId)?.let { rpcRequestId ->
            rpcRequestIdToCommitment[rpcRequestId]
        }
    }

    /**
     * Removes any registered `accountSubscribe` attempt for the given `accountKey`
     */
    fun clearAccountSubscription(accountKey: PublicKey) {
        val rpcRequestId = accountKeyToRpcRequestIdMap.removeKey(accountKey)
        if (rpcRequestId != null) {
            rpcRequestIdToSubscriptionId.removeKey(rpcRequestId)
            rpcRequestIdToCommitment.remove(rpcRequestId)
        }
    }

    /**
     * Removes any registered `signatureSubscribe` attempt for the given `accountKey`
     */
    fun clearSignatureSubscription(transactionSignature: TransactionSignature) {
        val rpcRequestId = transactionSignatureToRpcRequestIdMap.removeKey(transactionSignature)
        if (rpcRequestId != null) {
            rpcRequestIdToSubscriptionId.removeKey(rpcRequestId)
            rpcRequestIdToCommitment.remove(rpcRequestId)
        }
    }

    /**
     * Clears all entries from the registry
     */
    fun clear() {
        accountKeyToRpcRequestIdMap.clear()
        transactionSignatureToRpcRequestIdMap.clear()
        rpcRequestIdToSubscriptionId.clear()
        rpcRequestIdToCommitment.clear()
    }
}