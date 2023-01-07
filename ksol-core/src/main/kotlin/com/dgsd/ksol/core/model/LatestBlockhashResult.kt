package com.dgsd.ksol.core.model

/**
 * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api#getlatestblockhash">json-rpc API</a>
 */
data class LatestBlockhashResult(

  /**
   * A recent block hash from the ledger
   */
  val blockhash: String,


  /**
   * Last block height at which the blockhash will be valid
   */
  val lastValidBlockHeight: Long,
)