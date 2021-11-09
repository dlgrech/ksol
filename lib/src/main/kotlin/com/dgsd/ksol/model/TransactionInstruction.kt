package com.dgsd.ksol.model

data class TransactionInstruction(
    val programAccount: PublicKey,
    val inputData: ByteArray,
    val inputAccounts: List<PublicKey>,
) {
    override fun equals(other: Any?): Boolean {
        return if (this === other) {
            true
        } else if (other !is TransactionInstruction) {
            false
        } else {
            this.programAccount == other.programAccount &&
                    this.inputData.contentEquals(other.inputData) &&
                    this.inputAccounts == other.inputAccounts
        }
    }

    override fun hashCode(): Int {
        var result = programAccount.hashCode()
        result = 31 * result + inputData.contentHashCode()
        result = 31 * result + inputAccounts.hashCode()
        return result
    }
}