package com.dgsd.ksol.model

data class Transaction(
    val signatures: List<String>,
    val message: TransactionMessage,
) {

    val id: TransactionSignature = signatures.first()
}