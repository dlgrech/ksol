package com.dgsd.ksol.core.model

import java.time.OffsetDateTime

data class Transaction(
    val slot: Long,
    val blockTime: OffsetDateTime?,
    val signatures: List<String>,
    val message: TransactionMessage,
    val metadata: TransactionMetadata,
) {

    init {
        require(signatures.isNotEmpty()) {
            "No signatures found"
        }
    }

    val id: TransactionSignature = signatures.first()
}