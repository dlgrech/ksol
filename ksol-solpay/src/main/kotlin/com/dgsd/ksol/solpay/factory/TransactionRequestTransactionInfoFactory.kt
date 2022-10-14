package com.dgsd.ksol.solpay.factory

import com.dgsd.ksol.core.LocalTransactions
import com.dgsd.ksol.core.model.LocalTransaction
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.solpay.api.TransactionRequestTransactionDetailsResponse
import com.dgsd.ksol.solpay.model.SolPayTransactionInfo

internal object TransactionRequestTransactionInfoFactory {

  fun create(
    signingWalletAddress: PublicKey,
    response: TransactionRequestTransactionDetailsResponse
  ): SolPayTransactionInfo {
    return SolPayTransactionInfo(
      message = response.message,
      transaction = deserializeTransaction(signingWalletAddress, response.transactionBase64)
    )
  }

  private fun deserializeTransaction(
    signingWalletAddress: PublicKey,
    base64Transaction: String
  ): LocalTransaction {
    val transaction = LocalTransactions.deserializeTransaction(base64Transaction)
    transaction.validateTransactionRequest(signingWalletAddress)
    return transaction
  }

  private fun LocalTransaction.validateTransactionRequest(signingWalletAddress: PublicKey) {
    if (signatures.isNotEmpty()) {
      checkNotNull(message.recentBlockhash) { "Missing recent blockhash!" }

      val feePayer = message.accountKeys.singleOrNull { it.isFeePayer }
      checkNotNull(feePayer) { "Missing fee payer!" }
      check(feePayer.publicKey == signingWalletAddress) { "Invalid fee payer" }

      signatures.forEachIndexed { index, _ ->
        val accountKey = message.accountKeys[index].publicKey
        if (accountKey != signingWalletAddress) {
          check(LocalTransactions.isValidSignature(this, index)) {
            "Invalid signature at index $index"
          }
        }
      }
    }
  }
}