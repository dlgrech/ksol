package com.dgsd.ksol.model

data class Transaction(
    val signatures: List<TransactionSignature>,
    val message: TransactionMessage,
) {

    val id: TransactionSignature = signatures.first()
}