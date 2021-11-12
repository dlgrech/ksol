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
        const val GET_MULTIPLE_ACCOUNTS = "getMultipleAccounts"
        const val GET_PROGRAM_ACCOUNTS = "getProgramAccounts"
        const val GET_RECENT_BLOCKHASH = "getRecentBlockhash"
        const val GET_SIGNATURES_FOR_ADDRESS = "getSignaturesForAddress"
        const val GET_SIGNATURE_STATUSES = "getSignatureStatuses"
        const val GET_SUPPLY = "getSupply"
        const val GET_TRANSACTION = "getTransaction"
        const val GET_TRANSACTION_COUNT = "getTransactionCount"
        const val REQUEST_AIRDROP = "requestAirdrop"
        const val SEND_TRANSACTION = "sendTransaction"
    }

    object Subscriptions {
        const val ACCOUNT_SUBSCRIBE = "accountSubscribe"
        const val ACCOUNT_UNSUBSCRIBE = "accountUnsubscribe"
        const val SIGNATURE_SUBSCRIBE = "signatureSubscribe"
        const val SIGNATURE_UNSUBSCRIBE = "signatureUnsubscribe"
    }

    object Encodings {
        const val BASE64 = "base64"
        const val JSON = "json"
    }
}