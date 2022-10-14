package com.dgsd.ksol.cli.rpc

import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.model.AccountCirculatingStatus
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import kotlinx.coroutines.runBlocking

class GetLargestAccountsCommand() : CliktCommand(
    name = "getLargestAccounts"
) {

    private val api by requireObject<SolanaApi>()

    private val commitment by commitmentOption()
    private val circulatingStatus by option(
        "--circulating-status",
        help = "Passed as an argument to RPC calls that require a circulating status input"
    ).choice(
        "circulating" to AccountCirculatingStatus.CIRCULATING,
        "non-circulating" to AccountCirculatingStatus.NON_CIRCULATING
    )

    override fun run() = runBlocking {
        echo(api.getLargestAccounts(circulatingStatus, commitment))
    }
}