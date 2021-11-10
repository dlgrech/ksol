package com.dgsd.ksol.model

data class TransactionHeader(
    val numRequiredSignatures: Int,
    val numReadonlySignedAccounts: Int,
    val numReadonlyUnsignedAccounts: Int,
) {

    companion object {

        internal fun createFrom(
            accountKeys: List<TransactionAccountMetadata>
        ): TransactionHeader {
            return TransactionHeader(
                numRequiredSignatures = accountKeys.count { it.isSigner },
                numReadonlySignedAccounts = accountKeys.count { it.isSigner && !it.isWritable },
                numReadonlyUnsignedAccounts = accountKeys.count { !it.isSigner && !it.isWritable },
            )
        }
    }
}