package com.dgsd.android.solar.model

import com.dgsd.ksol.model.TransactionSignature

sealed interface TransactionViewState {

  data class Loading(val transactionSignature: TransactionSignature?) : TransactionViewState

  data class Error(val transactionSignature: TransactionSignature?) : TransactionViewState

  data class Transaction(
    val transactionSignature: TransactionSignature,
    val direction: Direction,
    val displayAccountText: CharSequence,
    val amountText: CharSequence,
    val dateText: CharSequence?,
  ) : TransactionViewState {
    enum class Direction {
      INCOMING,
      OUTGOING,
      NONE,
    }
  }
}