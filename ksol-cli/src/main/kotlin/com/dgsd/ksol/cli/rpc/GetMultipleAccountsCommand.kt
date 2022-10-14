package com.dgsd.ksol.cli.rpc

import com.dgsd.ksol.SolanaApi
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import kotlinx.coroutines.runBlocking

class GetMultipleAccountsCommand() : CliktCommand(
    name = "getMultipleAccounts"
) {

    private val api by requireObject<SolanaApi>()

    private val commitment by commitmentOption()
    private val accounts by multipleAccounts()

    override fun run() = runBlocking {
        echo(api.getMultipleAccounts(accounts, commitment))
    }
}