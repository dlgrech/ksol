package com.dgsd.ksol.cli.rpc

import com.dgsd.ksol.SolanaApi
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.long
import kotlinx.coroutines.runBlocking

class RequestAirdropCommand() : CliktCommand(
    name = "requestAirdrop"
) {

    private val api by requireObject<SolanaApi>()

    private val account by accountArgument()
    private val lamports by argument(
        name = "LAMPORTS"
    ).long()
    private val commitment by commitmentOption()

    override fun run() = runBlocking {
        echo(api.requestAirdrop(account, lamports, commitment))
    }
}