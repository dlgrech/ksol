package com.dgsd.ksol.model

data class TransactionInstruction(
    val programAccount: PublicKey,
    val inputData: ByteArray,
    val inputAccounts: List<PublicKey>,
)