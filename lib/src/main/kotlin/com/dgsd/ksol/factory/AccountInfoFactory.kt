package com.dgsd.ksol.factory

import com.dgsd.ksol.jsonrpc.types.AccountInfoResponse
import com.dgsd.ksol.model.AccountInfo
import com.dgsd.ksol.model.PublicKey

internal object AccountInfoFactory {

    fun create(response: AccountInfoResponse?): AccountInfo? {
        return if (response == null) {
            null
        } else {
            AccountInfo(
                ownerHash = PublicKey.fromBase58(response.ownerHash),
                lamports = response.lamports,
                isExecutable = response.isExecutable,
                rentEpoch = response.rentEpoch,
                accountData = response.dataAndFormat.firstOrNull() ?: ""
            )
        }
    }
}