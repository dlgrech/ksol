package com.dgsd.ksol.jsonrpc

/**
 * Constants used when interacting with the Solana JSON RPC
 *
 * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api">json-rpc API</a>
 */
internal object SolanaJsonRpcConstants {

    const val VERSION = "2.0"

    object Methods {
        const val GET_RECENT_BLOCKHASH = "getRecentBlockhash"
        const val GET_BLOCK_TIME = "getBlockTime"
        const val GET_BLOCK_HEIGHT = "getBlockHeight"
        const val GET_LARGEST_ACCOUNTS = "getLargestAccounts"
    }
}