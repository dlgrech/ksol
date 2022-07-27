package com.dgsd.android.solar.transaction.details

data class TransactionAccountViewState(
  val accountDisplayText: CharSequence,
  val isWriter: Boolean,
  val isProgram: Boolean,
  val isSigner: Boolean,
  val balanceAfterText: CharSequence?,
)