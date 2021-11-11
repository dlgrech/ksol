package com.dgsd.ksol

import com.dgsd.ksol.model.AccountInfo
import com.dgsd.ksol.model.Commitment
import com.dgsd.ksol.model.PublicKey
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
        commitment: Commitment = Commitment.PROCESSED,
    ): Flow<AccountInfo>

    /**
     * Unsubscribe from account change notifications
     *
     * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api#accountunsubscribe">Subscription Websocket API</a>
     */
    fun accountUnsubscribe(accountKey: PublicKey)
}