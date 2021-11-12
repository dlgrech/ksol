package com.dgsd.ksol

import com.dgsd.ksol.model.*
import kotlinx.coroutines.flow.Flow

/**
 * API for subscribing to changes to the Solana blockchain, using the Subscription Websocket API
 *
 * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api#subscription-websocket">Subscription Websocket API</a>
 */
interface SolanaSubscription {

    /**
     * Connect to the underlying Solana Websocket API.
     */
    fun connect()

    /**
     * Disconnect from the underlying Solana Websocket API.
     */
    fun disconnect()

    /**
     * @return `true` if the subscription has been connected already (that is, [connect] has been called),
     * `false` otherwise
     */
    fun isConnected(): Boolean

    /**
     * Subscribe to an account to receive notifications when the lamports or data for a given
     * account public key changes
     *
     * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api#accountsubscribe">Subscription Websocket API</a>
     */
    fun accountSubscribe(
        accountKey: PublicKey,
        commitment: Commitment = Commitment.FINALIZED,
    ): Flow<AccountInfo>

    /**
     * Unsubscribe from account change notifications
     *
     * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api#accountunsubscribe">Subscription Websocket API</a>
     */
    fun accountUnsubscribe(accountKey: PublicKey)

    /**
     * Subscribe to a transaction signature to receive notification when the transaction is confirmed.
     *
     * On signatureNotification, the subscription is automatically cancelled
     *
     * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api#signaturesubscribe">Subscription Websocket API</a>
     */
    fun signatureSubscribe(
        signature: TransactionSignature,
        commitment: Commitment = Commitment.FINALIZED,
    ): Flow<TransactionSignatureStatus>

    /**
     * Unsubscribe from signature confirmation notification
     *
     * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api#signatureunsubscribe">Subscription Websocket API</a>
     */
    fun signatureUnsubscribe(signature: TransactionSignature)
}