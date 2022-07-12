package com.dgsd.ksol.serialization

import com.dgsd.ksol.model.*
import com.dgsd.ksol.utils.DecodingUtils
import com.dgsd.ksol.utils.ShortVecEncoding
import java.nio.ByteBuffer

/**
 * Helper methods for serializing [LocalTransaction] objects in the format expected by the Solana json-rpc
 */
internal object LocalTransactionSerializer {

    private const val TRANSACTION_SIGNATURE_LENGTH = 64
    private const val MESSAGE_HEADER_LENGTH = 3
    private const val RECENT_BLOCKHASH_LENGTH = 32

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
                    RECENT_BLOCKHASH_LENGTH +
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
            .put(DecodingUtils.decodeBase58(message.recentBlockhash))
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

    private class CompiledTransactionInstruction(
        val programIdIndex: Byte,
        val keyIndiciesLength: ByteArray,
        val keyIndicies: ByteArray,
        val inputDataLength: ByteArray,
        val inputData: ByteArray,
    ) {

        val totalLength = 1 + keyIndiciesLength.size + keyIndicies.size + inputDataLength.size + inputData.size

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