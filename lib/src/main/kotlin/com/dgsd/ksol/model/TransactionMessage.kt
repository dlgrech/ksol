package com.dgsd.ksol.model

data class TransactionMessage(
    val header: TransactionHeader,
    val accountKeys: List<TransactionAccountMetadata>,
    val recentBlockhash: String,
    val instructions: List<TransactionInstruction>,
) {

    val requiredSigners = accountKeys.take(header.numRequiredSignatures)
}