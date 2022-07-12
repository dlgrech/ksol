package com.dgsd.android.solar.model

import com.dgsd.ksol.model.Transaction
import com.dgsd.ksol.model.TransactionSignature

/**
 * An app-specific representation of a transaction
 */
sealed interface TransactionInfo {

    val signature: TransactionSignature

    data class UnknownTransaction(override val signature: TransactionSignature) : TransactionInfo

    data class FullTransaction(val transaction: Transaction) : TransactionInfo {

        override val signature = transaction.id
    }
}