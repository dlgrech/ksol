package com.dgsd.ksol

import com.dgsd.ksol.model.Cluster
import com.dgsd.ksol.model.Commitment

/**
 * Create a default implementation of [SolanaApi] using the given [Cluster]
 */
fun SolanaApi(cluster: Cluster): SolanaApi {
    return SolanaApiImpl(cluster)
}

/**
 * API for interacting with the Solana blockchain, using the JSON-RPC API
 *
 * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api">json-rpc API</a>
 */
interface SolanaApi {

    /**
     * Returns a recent block hash from the ledger, and a fee schedule that can be used to compute the cost of submitting a transaction using it.
     *
     * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api#getrecentblockhash">json-rpc API</a>
     */
    suspend fun getRecentBlockhash(commitment: Commitment = Commitment.FINALIZED): String
}