package com.dgsd.ksol.core.model

data class TransactionMetadata(
  val fee: Lamports,
  val accountBalances: List<Balance>,
  val logMessages: List<String>,
) {

  data class Balance(
    val accountKey: PublicKey,

    val balanceBefore: Lamports,

    val balanceAfter: Lamports
  )
}