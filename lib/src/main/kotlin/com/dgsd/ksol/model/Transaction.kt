package com.dgsd.ksol.model

data class Transaction(
    val signatures: List<String>,
    val message: TransactionMessage,
    val metadata: TransactionMetadata?,
) {

    init {
        require(signatures.isNotEmpty()) {
            "No signatures found"
        }
    }

    val id: TransactionSignature = signatures.first()
}