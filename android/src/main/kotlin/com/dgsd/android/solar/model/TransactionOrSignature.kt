package com.dgsd.android.solar.model

import com.dgsd.ksol.model.Transaction
import com.dgsd.ksol.model.TransactionSignature

class TransactionOrSignature private constructor(
  private val signature: TransactionSignature?,
  private val transaction: Transaction?
) {

  constructor(signature: TransactionSignature) : this(signature, null)
  constructor(transaction: Transaction) : this(null, transaction)

  fun transactionOrThrow(): Transaction {
    return requireNotNull(transaction)
  }

  fun signature(): TransactionSignature {
    return requireNotNull(signature ?: transaction?.id)
  }
}