package com.dgsd.ksol

import com.dgsd.ksol.model.*
import com.dgsd.ksol.programs.memo.MemoProgram
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
    memo: String?,
    recentBlockhash: PublicKey,
  ): LocalTransaction {
    val transferInstruction = SystemProgram.transfer(
      sender.publicKey,
      recipient,
      lamports
    )

    val memoInstruction = if (memo == null) {
      null
    } else {
      MemoProgram.createInstruction(memo)
    }

    val accountKeys = listOfNotNull(
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
      if (memoInstruction == null) {
        null
      } else {
        TransactionAccountMetadata(
          memoInstruction.programAccount,
          isSigner = false,
          isWritable = false,
          isFeePayer = false
        )
      },
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
      listOfNotNull(memoInstruction, transferInstruction)
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
  fun deserializeTransaction(
    base64EncodedTransaction: String
  ): LocalTransaction {
    val transactionBytes = DecodingUtils.decodeBase64(base64EncodedTransaction)
    return deserializeTransaction(transactionBytes)
  }

  /**
   * Takes a serialized [LocalTransaction] and returns the original [LocalTransaction]
   */
  fun deserializeTransaction(
    transactionBytes: ByteArray
  ): LocalTransaction {
    return LocalTransactionSerialization.deserialize(transactionBytes)
  }

  /**
   * Takes a [LocalTransaction] and serializes it to a [ByteArray]
   */
  fun serialize(localTransaction: LocalTransaction): ByteArray {
    return LocalTransactionSerialization.serialize(localTransaction)
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

  /**
   * Signs the given [LocalTransaction] and places the resulting signature into a new transaction
   */
  fun sign(input: LocalTransaction, keyPair: KeyPair): LocalTransaction {
    val indexOfSigner = input.message.accountKeys.indexOfFirst { it.publicKey == keyPair.publicKey }
    require(indexOfSigner >= 0) { "Signer not expected" }

    val messageBytes = LocalTransactionSerialization.serialize(input.message)
    val newSignatureBytes = SigningUtils.sign(messageBytes, keyPair.privateKey)

    val newSignatures = input.signatures.mapIndexed { index, existingSignature ->
      if (index == indexOfSigner) {
        EncodingUtils.encodeBase58(newSignatureBytes)
      } else {
        existingSignature
      }
    }

    return LocalTransaction(
      signatures = newSignatures,
      message = input.message
    )
  }

  /**
   * Signs a generic [ByteArray] with the given [KeyPair]. Intended for signing portions of a
   * transaction, such as an individual message
   */
  fun sign(input: ByteArray, keyPair: KeyPair): ByteArray {
    return SigningUtils.sign(input, keyPair.privateKey)
  }
}