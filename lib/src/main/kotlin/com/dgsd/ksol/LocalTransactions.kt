package com.dgsd.ksol

import com.dgsd.ksol.model.*
import com.dgsd.ksol.programs.system.SystemProgram
import com.dgsd.ksol.serialization.LocalTransactionSerializer
import com.dgsd.ksol.utils.EncodingUtils
import com.dgsd.ksol.utils.SigningUtils

/**
 * Helper methods for creating [Transaction] objects
 */
object LocalTransactions {

    /**
     * Creates a [LocalTransactions] that will transfer `lamports` from `sender` to `recipient`
     */
    fun createTransferTransaction(
        sender: KeyPair,
        recipient: PublicKey,
        lamports: Lamports,
        recentBlockhash: String,
    ): LocalTransaction {
        val transferInstruction = SystemProgram.transfer(
            sender.publicKey,
            recipient,
            lamports
        )

        val accountKeys = listOf(
            TransactionAccountMetadata(
                sender.publicKey,
                isSigner = true,
                isWritable = true,
                isFeePayer = true
            ),
            TransactionAccountMetadata(
                recipient,
                isSigner = false,
                isWritable = true,
                isFeePayer = false
            ),
            TransactionAccountMetadata(
                transferInstruction.programAccount,
                isSigner = false,
                isWritable = false,
                isFeePayer = false
            )
        )

        val header = TransactionHeader.createFrom(accountKeys)

        val message = TransactionMessage(
            header,
            accountKeys,
            recentBlockhash,
            listOf(transferInstruction)
        )

        val serializedMessage = LocalTransactionSerializer.serialize(message)
        val signatureBytes = SigningUtils.sign(serializedMessage, sender.privateKey)
        val signatures = listOf(EncodingUtils.encodeBase58(signatureBytes))

        return LocalTransaction(signatures, message)
    }
}