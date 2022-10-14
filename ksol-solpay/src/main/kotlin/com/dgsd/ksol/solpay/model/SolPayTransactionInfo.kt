package com.dgsd.ksol.solpay.model

import com.dgsd.ksol.model.LocalTransaction

data class SolPayTransactionInfo(

  /**
   * The transaction that should be submitted as part of this request.
   *
   * Note that this transaction will need to be signed with a private key before submission
   */
  val transaction: LocalTransaction,

  /**
   * Describes the nature of the transaction response.
   *
   * For example, this might be the name of an item being purchased,
   * a discount applied to the purchase, or a thank you note
   */
  val message: String?
)