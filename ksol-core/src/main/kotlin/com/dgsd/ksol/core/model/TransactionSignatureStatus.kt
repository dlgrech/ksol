package com.dgsd.ksol.core.model

sealed class TransactionSignatureStatus {

  abstract val signature: TransactionSignature

  data class UnknownTransaction(
    override val signature: TransactionSignature
  ) : TransactionSignatureStatus()

  data class Confirmed(
    override val signature: TransactionSignature,
    val slot: Long?,
    val commitment: Commitment?,
    val errorMessage: String?
  ) : TransactionSignatureStatus()
}