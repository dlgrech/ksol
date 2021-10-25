package com.dgsd.ksol.jsonrpc

/**
 * Constants used when interacting with the Solana JSON RPC
 *
 * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api">json-rpc API</a>
 */
internal object SolanaJsonRpcConstants {

    val VERSION = "2.0"

    object Methods {
        const val GET_RECENT_BLOCKHASH = "getRecentBlockhash"
    }
}