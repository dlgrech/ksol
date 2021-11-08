package com.dgsd.ksol.cli.rpc

import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.model.PublicKey
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.transformAll
import com.github.ajalt.clikt.parameters.arguments.validate
import kotlinx.coroutines.runBlocking

class GetMultipleAccountsCommand() : CliktCommand(
    name = "getMultipleAccounts"
) {

    private val api by requireObject<SolanaApi>()

    private val commitment by commitmentOption()
    private val accounts by argument(
        "--accounts",
        help = "ACCOUNTS"
    ).multiple(
        required = true
    ).transformAll { accountHashes ->
        accountHashes.map { PublicKey.fromBase58(it) }
    }.validate {
        require(it.isNotEmpty()) {
            "No accounts passed"
        }
    }

    override fun run() = runBlocking {
        echo(api.getMultipleAccounts(accounts, commitment))
    }
}