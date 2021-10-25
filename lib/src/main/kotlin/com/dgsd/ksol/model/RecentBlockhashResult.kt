package com.dgsd.ksol.model

/**
 * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api#getrecentblockhash">json-rpc API</a>
 */
data class RecentBlockhashResult(

    /**
     * A recent block hash from the ledger
     */
    val blockhash: String,


    /**
     * A fee schedule that can be used to compute the cost of submitting a transaction using the block hash.
     *
     * This value is the number of lamports per signature
     */
    val fee: Lamports,
)