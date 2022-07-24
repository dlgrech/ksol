package com.dgsd.android.solar.model

import com.dgsd.ksol.model.TransactionSignature

data class TransactionViewState(
  val transactionSignature: TransactionSignature,
  val direction: Direction,
  val displayAccountText: CharSequence,
  val amountText: CharSequence,
  val dateText: CharSequence?,
) {

  enum class Direction {
    INCOMING,
    OUTGOING,
    NONE,
  }
}