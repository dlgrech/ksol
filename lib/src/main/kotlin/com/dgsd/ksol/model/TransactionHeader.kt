package com.dgsd.ksol.model

data class TransactionHeader(
    val numRequiredSignatures: Int,
    val numReadonlySignedAccounts: Int,
    val numReadonlyUnsignedAccounts: Int,
)