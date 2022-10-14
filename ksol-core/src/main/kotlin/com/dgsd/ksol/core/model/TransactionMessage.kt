package com.dgsd.ksol.core.model

data class TransactionMessage(
  val header: TransactionHeader,
  val accountKeys: List<TransactionAccountMetadata>,
  val recentBlockhash: PublicKey,
  val instructions: List<TransactionInstruction>,
) {

  init {
    require(accountKeys.isNotEmpty()) {
      "No account keys passed"
    }

    require(instructions.isNotEmpty()) {
      "No instructions passed"
    }
  }
}