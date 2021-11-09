package com.dgsd.ksol.factory

import com.dgsd.ksol.jsonrpc.types.GetTransactionResponseBody
import com.dgsd.ksol.jsonrpc.types.TransactionResponse
import com.dgsd.ksol.model.*
import com.dgsd.ksol.utils.DecodingUtils

internal object TransactionFactory {

    fun create(response: GetTransactionResponseBody?): Transaction? {
        return create(response?.transaction)
    }

    fun create(response: TransactionResponse?): Transaction? {
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

        val accountKeys = response.accountKeys.map(PublicKey::fromBase58)

        val instructions = response.instructions.map {
            TransactionInstruction(
                programAccount = accountKeys[it.programIdIndex],
                inputData = DecodingUtils.decodeBase58(it.inputData),
                inputAccounts = it.accounts.map(accountKeys::get)
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