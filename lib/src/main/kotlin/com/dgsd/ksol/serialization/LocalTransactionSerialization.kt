package com.dgsd.ksol.serialization

import com.dgsd.ksol.model.*
import com.dgsd.ksol.utils.DecodingUtils
import com.dgsd.ksol.utils.EncodingUtils
import com.dgsd.ksol.utils.ShortVecEncoding
import java.nio.ByteBuffer

/**
 * Helper methods for serializing [LocalTransaction] objects in the format expected by the Solana json-rpc
 */
internal object LocalTransactionSerialization {

  private const val TRANSACTION_SIGNATURE_LENGTH = 64
  private const val MESSAGE_HEADER_LENGTH = 3

  fun serialize(transaction: LocalTransaction): ByteArray {
    val serializedMessage = serialize(transaction.message)

    val signaturesSize = transaction.signatures.size
    val signaturesSizeCompacted = ShortVecEncoding.encodeLength(signaturesSize)

    val output = ByteBuffer.allocate(
      signaturesSizeCompacted.size + signaturesSize * TRANSACTION_SIGNATURE_LENGTH + serializedMessage.size
    )

    output.put(signaturesSizeCompacted)
    transaction.signatures.map(DecodingUtils::decodeBase58).forEach(output::put)
    output.put(serializedMessage)

    return output.array()
  }

  fun serialize(message: TransactionMessage): ByteArray {
    val accountKeysSize = message.accountKeys.size
    val accountKeysSizeCompacted = ShortVecEncoding.encodeLength(accountKeysSize)

    val instructionsSize = message.instructions.size
    val instructionsSizeCompacted = ShortVecEncoding.encodeLength(instructionsSize)

    val compiledInstructions = message.instructions.map {
      CompiledTransactionInstruction.create(message.accountKeys, it)
    }

    val output = ByteBuffer.allocate(
      MESSAGE_HEADER_LENGTH +
        PublicKey.PUBLIC_KEY_LENGTH +
        accountKeysSizeCompacted.size +
        (accountKeysSize * PublicKey.PUBLIC_KEY_LENGTH) +
        instructionsSizeCompacted.size +
        compiledInstructions.sumOf { it.totalLength }
    )

    val accountKeysOutput = ByteBuffer.allocate(accountKeysSize * PublicKey.PUBLIC_KEY_LENGTH)
    message.accountKeys.map { it.publicKey.key }.forEach(accountKeysOutput::put)

    output
      .put(serialize(message.header))
      .put(accountKeysSizeCompacted)
      .put(accountKeysOutput.array())
      .put(DecodingUtils.decodeBase58(message.recentBlockhash.toBase58String()))
      .put(instructionsSizeCompacted)
    compiledInstructions.forEach { compiledInstruction ->
      output
        .put(compiledInstruction.programIdIndex)
        .put(compiledInstruction.keyIndiciesLength)
        .put(compiledInstruction.keyIndicies)
        .put(compiledInstruction.inputDataLength)
        .put(compiledInstruction.inputData)
    }

    return output.array()
  }

  private fun serialize(header: TransactionHeader): ByteArray {
    return byteArrayOf(
      header.numRequiredSignatures.toByte(),
      header.numReadonlySignedAccounts.toByte(),
      header.numReadonlyUnsignedAccounts.toByte(),
    )
  }

  fun deserialize(input: ByteArray): LocalTransaction {
    val buffer = ByteBuffer.wrap(input)

    return LocalTransaction(
      deserializeSignatures(buffer),
      deserializeMessage(buffer)
    )
  }

  private fun deserializeSignatures(buffer: ByteBuffer): List<String> {
    val signatureCount = ShortVecEncoding.decodeLength(buffer)

    val signatures = mutableListOf<String>()
    repeat(signatureCount) {
      val signatureBytes = ByteArray(TRANSACTION_SIGNATURE_LENGTH).apply { buffer.get(this) }
      signatures.add(EncodingUtils.encodeBase58(signatureBytes))
    }

    return signatures
  }

  private fun deserializeMessage(buffer: ByteBuffer): TransactionMessage {
    val header = deserializeHeader(buffer)

    val accountKeysCount = ShortVecEncoding.decodeLength(buffer)
    val accountKeys = 0.until(accountKeysCount).map {
      val accountBytes = ByteArray(PublicKey.PUBLIC_KEY_LENGTH).apply { buffer.get(this) }
      PublicKey.fromBase58(EncodingUtils.encodeBase58(accountBytes))
    }

    val signerPublicKeys = accountKeys.take(header.numRequiredSignatures)
    val nonSignerPublicKeys = accountKeys.drop(signerPublicKeys.size)

    val signers = signerPublicKeys.mapIndexed { index, key ->
      TransactionAccountMetadata(
        key,
        isSigner = true,
        isFeePayer = index == 0,
        isWritable = index < (signerPublicKeys.size - header.numReadonlySignedAccounts)
      )
    }
    val nonSigners = nonSignerPublicKeys.mapIndexed { index, key ->
      TransactionAccountMetadata(
        key,
        isSigner = false,
        isFeePayer = false,
        isWritable = index < (nonSignerPublicKeys.size - header.numReadonlyUnsignedAccounts)
      )
    }

    val accountMetadata = TransactionAccountMetadata.collapse(signers + nonSigners)

    val blockhash = ByteArray(PublicKey.PUBLIC_KEY_LENGTH).apply { buffer.get(this) }

    val instructionCount = ShortVecEncoding.decodeLength(buffer)
    val instructions = mutableListOf<TransactionInstruction>()

    repeat(instructionCount) {
      val programIdIndex = buffer.get().toInt()
      val accountCount = ShortVecEncoding.decodeLength(buffer)
      val accountIndexes = ByteArray(accountCount).apply { buffer.get(this) }.map { it.toInt() }

      val inputDataLength = ShortVecEncoding.decodeLength(buffer)
      val inputData = ByteArray(inputDataLength).apply { buffer.get(this) }

      instructions.add(
        TransactionInstruction(
          programAccount = accountKeys[programIdIndex],
          inputAccounts = accountIndexes.map { accountKeys[it] },
          inputData = inputData,
        )
      )
    }

    return TransactionMessage(
      header,
      accountKeys = accountMetadata,
      recentBlockhash = PublicKey.fromBase58(EncodingUtils.encodeBase58(blockhash)),
      instructions = instructions
    )
  }

  private fun deserializeHeader(buffer: ByteBuffer): TransactionHeader {
    return TransactionHeader(
      numRequiredSignatures = buffer.get().toInt(),
      numReadonlySignedAccounts = buffer.get().toInt(),
      numReadonlyUnsignedAccounts = buffer.get().toInt()
    )
  }

  private class CompiledTransactionInstruction(
    val programIdIndex: Byte,
    val keyIndiciesLength: ByteArray,
    val keyIndicies: ByteArray,
    val inputDataLength: ByteArray,
    val inputData: ByteArray,
  ) {

    val totalLength =
      1 + keyIndiciesLength.size + keyIndicies.size + inputDataLength.size + inputData.size

    companion object {
      fun create(
        messageAccountKeys: List<TransactionAccountMetadata>,
        instruction: TransactionInstruction,
      ): CompiledTransactionInstruction {
        val programInstruction = TransactionAccountMetadata.indexOf(
          messageAccountKeys,
          instruction.programAccount
        )

        val keySize = instruction.inputAccounts.size
        val keyIndicies = instruction.inputAccounts
          .map { TransactionAccountMetadata.indexOf(messageAccountKeys, it) }
          .map { it.toByte() }
          .toByteArray()

        return CompiledTransactionInstruction(
          programIdIndex = programInstruction.toByte(),
          keyIndiciesLength = ShortVecEncoding.encodeLength(keySize),
          keyIndicies = keyIndicies,
          inputDataLength = ShortVecEncoding.encodeLength(instruction.inputData.size),
          inputData = instruction.inputData,
        )
      }
    }
  }
}