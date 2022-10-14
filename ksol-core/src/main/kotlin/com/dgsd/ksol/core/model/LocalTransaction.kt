package com.dgsd.ksol.core.model

/**
 * Like a [Transaction], but used for constructing on the local machine
 */
data class LocalTransaction(
    val signatures: List<String>,
    val message: TransactionMessage,
) {

    init {
        require(signatures.isNotEmpty()) {
            "No signatures found"
        }
    }

    val id: TransactionSignature = signatures.first()
}