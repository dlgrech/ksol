package com.dgsd.ksol.factory

import com.dgsd.ksol.jsonrpc.types.GetTransactionResponseBody
import com.dgsd.ksol.jsonrpc.types.TransactionMetaResponse
import com.dgsd.ksol.jsonrpc.types.TransactionResponse
import com.dgsd.ksol.model.*
import com.dgsd.ksol.utils.DecodingUtils
import com.dgsd.ksol.utils.toOffsetDateTime

internal object TransactionFactory {

    fun create(response: GetTransactionResponseBody?): Transaction? {
        val transactionResponse = response?.transaction
        val metaResponse = response?.meta
        return if (transactionResponse == null || metaResponse == null) {
            null
        } else {
            val message = create(transactionResponse.message)
            val blockTime = response.blockTime?.toOffsetDateTime()
            Transaction(
                slot = response.slot,
                blockTime = blockTime,
                signatures = transactionResponse.signatures,
                message = message,
                metadata = create(message, metaResponse),
            )
        }
    }

    private fun create(response: TransactionResponse.Message): TransactionMessage {
        val header = TransactionHeader(
            numRequiredSignatures = response.header.numRequiredSignatures,
            numReadonlySignedAccounts = response.header.numReadonlySignedAccounts,
            numReadonlyUnsignedAccounts = response.header.numReadonlyUnsignedAccounts,
        )

        val publicKeys = response.accountKeys.map(PublicKey::fromBase58)
        val signerPublicKeys = publicKeys.take(header.numRequiredSignatures)
        val nonSignerPublicKeys = publicKeys.drop(signerPublicKeys.size)

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

        val accountKeys = TransactionAccountMetadata.collapse(signers + nonSigners)

        val instructions = response.instructions.map {
            TransactionInstruction(
                programAccount = accountKeys[it.programIdIndex].publicKey,
                inputData = DecodingUtils.decodeBase58(it.inputData),
                inputAccounts = it.accounts.map { accountIndex ->
                    accountKeys[accountIndex].publicKey
                }
            )
        }

        return TransactionMessage(
            header = header,
            accountKeys = accountKeys,
            recentBlockhash = PublicKey.fromBase58(response.recentBlockhash),
            instructions = instructions
        )
    }

    private fun create(
        message: TransactionMessage,
        response: TransactionMetaResponse,
    ): TransactionMetadata {
        return TransactionMetadata(
            fee = response.fee,
            logMessages = response.logMessages,
            accountBalances = message.accountKeys.mapIndexed { index, account ->
                TransactionMetadata.Balance(
                    account.publicKey,
                    response.preBalances[index],
                    response.postBalances[index],
                )
            }
        )
    }
}