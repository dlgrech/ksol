package com.dgsd.ksol

import com.dgsd.ksol.model.*
import com.dgsd.ksol.programs.SystemProgram
import com.dgsd.ksol.serialization.TransactionSerializer
import com.dgsd.ksol.utils.EncodingUtils
import com.dgsd.ksol.utils.SigningUtils

/**
 * Helper methods for creating [Transaction] objects
 */
object Transactions {

    /**
     * Creates a [Transactions] that will transfer `lamports` from `sender` to `recipient`
     */
    fun createTransferTransaction(
        sender: KeyPair,
        recipient: PublicKey,
        lamports: Lamports,
        recentBlockhash: String,
    ): Transaction {
        val transferInstruction = SystemProgram.transfer(
            sender.publicKey,
            recipient,
            lamports
        )

        val accountKeys = listOf(
            TransactionAccountMetadata(sender.publicKey, isSigner = true, isWritable = true),
            TransactionAccountMetadata(recipient, isSigner = false, isWritable = true),
            TransactionAccountMetadata(transferInstruction.programAccount, isSigner = false, isWritable = false)
        )

        val header = TransactionHeader.createFrom(accountKeys)

        val message = TransactionMessage(
            header,
            accountKeys,
            recentBlockhash,
            listOf(transferInstruction)
        )

        val serializedMessage = TransactionSerializer.serialize(message)
        val signatureBytes = SigningUtils.sign(serializedMessage, sender.privateKey)
        val signatures = listOf(EncodingUtils.encodeBase58(signatureBytes))

        return Transaction(signatures, message)
    }
}