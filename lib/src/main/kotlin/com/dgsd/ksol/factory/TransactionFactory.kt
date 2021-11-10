package com.dgsd.ksol.factory

import com.dgsd.ksol.jsonrpc.types.GetTransactionResponseBody
import com.dgsd.ksol.jsonrpc.types.TransactionResponse
import com.dgsd.ksol.model.*
import com.dgsd.ksol.utils.DecodingUtils

internal object TransactionFactory {

    fun create(response: GetTransactionResponseBody?): Transaction? {
        return create(response?.transaction)
    }

    private fun create(response: TransactionResponse?): Transaction? {
        return if (response == null) {
            null
        } else {
            Transaction(
                signatures = response.signatures,
                message = create(response.message)
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
                isWritable = index < (signerPublicKeys.size - header.numReadonlySignedAccounts)
            )
        }
        val nonSigners = nonSignerPublicKeys.mapIndexed { index, key ->
            TransactionAccountMetadata(
                key,
                isSigner = false,
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
            recentBlockhash = response.recentBlockhash,
            instructions = instructions
        )
    }
}