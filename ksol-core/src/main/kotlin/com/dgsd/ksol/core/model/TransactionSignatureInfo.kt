package com.dgsd.ksol.core.model

data class TransactionSignatureInfo(
    val signature: TransactionSignature,
    val slot: Long,
    val memo: String?,
    val blockTime: Long?,
    val errorMessage: String?
)