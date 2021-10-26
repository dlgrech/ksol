package com.dgsd.ksol.jsonrpc

/**
 * Constants used when interacting with the Solana JSON RPC
 *
 * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api">json-rpc API</a>
 */
internal object SolanaJsonRpcConstants {

    const val VERSION = "2.0"

    object Methods {
        const val GET_ACCOUNT_INFO = "getAccountInfo"
        const val GET_BALANCE = "getBalance"
        const val GET_BLOCK_TIME = "getBlockTime"
        const val GET_BLOCK_HEIGHT = "getBlockHeight"
        const val GET_LARGEST_ACCOUNTS = "getLargestAccounts"
        const val GET_MINIMUM_BALANCE_FOR_RENT_EXEMPTION = "getMinimumBalanceForRentExemption"
        const val GET_PROGRAM_ACCOUNTS = "getProgramAccounts"
        const val GET_RECENT_BLOCKHASH = "getRecentBlockhash"
        const val GET_SUPPLY = "getSupply"
        const val GET_TRANSACTION_COUNT = "getTransactionCount"
    }

    object Encodings {
        const val BASE64 = "base64"
    }
}