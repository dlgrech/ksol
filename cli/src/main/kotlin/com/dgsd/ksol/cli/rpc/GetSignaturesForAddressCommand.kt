package com.dgsd.ksol.cli.rpc

import com.dgsd.ksol.SolanaApi
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.types.int
import kotlinx.coroutines.runBlocking

class GetSignaturesForAddressCommand() : CliktCommand(
    name = "getSignaturesForAddress"
) {

    private val api by requireObject<SolanaApi>()

    private val account by accountArgument()
    private val commitment by commitmentOption()
    private val limit by option("--limit").int().default(1000).validate { it in 1..1000 }
    private val beforeTransaction by option("--before")
    private val untilTransaction by option("--until")

    override fun run() = runBlocking {
        api.getSignaturesForAddress(
            accountKey = account,
            limit = limit,
            before = beforeTransaction,
            until = untilTransaction,
            commitment = commitment
        ).forEach(::echo)
    }
}