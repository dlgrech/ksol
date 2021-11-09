package com.dgsd.ksol.cli.rpc

import com.dgsd.ksol.SolanaApi
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import kotlinx.coroutines.runBlocking

class GetSignatureStatusesCommand() : CliktCommand(
    name = "getSignatureStatuses"
) {

    private val api by requireObject<SolanaApi>()

    private val transactionSignatures by argument(
        "--signatures",
        help = "TRANSACTION_SIGNATURES"
    ).multiple(
        required = true
    )

    private val searchTransactionHistory by option(
        "--search-transaction-history"
    ).flag(default = true)

    override fun run() = runBlocking {
        api.getSignatureStatuses(
            transactionSignatures,
            searchTransactionHistory
        ).forEach(::echo)
    }
}