package com.dgsd.ksol.model

data class TransactionInstruction(
    val programAccount: PublicKey,
    val inputData: String,
    val inputAccounts: List<PublicKey>,
)