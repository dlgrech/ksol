package com.dgsd.ksol.model

/**
 * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api#configuring-state-commitment">json-rpc API</a>
 */
enum class Commitment {

    FINALIZED,

    CONFIRMED,

    PROCESSED,
}