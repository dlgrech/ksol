package com.dgsd.ksol

import com.dgsd.ksol.model.*
import com.dgsd.ksol.programs.system.SystemProgram
import com.dgsd.ksol.serialization.LocalTransactionSerialization
import com.dgsd.ksol.utils.DecodingUtils
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
    recentBlockhash: PublicKey,
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

    val serializedMessage = LocalTransactionSerialization.serialize(message)
    val signatureBytes = SigningUtils.sign(serializedMessage, sender.privateKey)
    val signatures = listOf(EncodingUtils.encodeBase58(signatureBytes))

    return LocalTransaction(signatures, message)
  }

  /**
   * Takes a serialized [LocalTransaction], which has been base64 encoded, and returns the
   * original [LocalTransaction]
   */
  fun deserializeTransferTransaction(
    base64EncodedTransaction: String
  ): LocalTransaction {
    val transactionBytes = DecodingUtils.decodeBase64(base64EncodedTransaction)
    return LocalTransactionSerialization.deserialize(transactionBytes)
  }

  /**
   * @return `true` if the account at the given `index` has a valid signature as part of the
   * transaction
   *
   * @throws IndexOutOfBoundsException if there is no account at the given index
   */
  fun isValidSignature(transaction: LocalTransaction, index: Int): Boolean {
    val signature = DecodingUtils.decodeBase58(transaction.signatures[index])
    val key = transaction.message.accountKeys[index].publicKey
    val messageBytes = LocalTransactionSerialization.serialize(transaction.message)

    return SigningUtils.isValidSignature(messageBytes, signature, key)
  }
}