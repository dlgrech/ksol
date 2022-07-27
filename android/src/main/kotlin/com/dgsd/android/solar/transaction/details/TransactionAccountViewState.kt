package com.dgsd.android.solar.transaction.details

import com.dgsd.ksol.model.PublicKey

data class TransactionAccountViewState(
  val accountKey: PublicKey,
  val accountDisplayText: CharSequence,
  val isWriter: Boolean,
  val isProgram: Boolean,
  val isSigner: Boolean,
  val isFeePayer: Boolean,
  val balanceAfterText: CharSequence?,
)