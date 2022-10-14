package com.dgsd.ksol.cli.rpc

import com.dgsd.ksol.SolanaApi
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking

class SignatureSubscribeCommand() : CliktCommand(
    name = "signatureSubscribe",
    help = "Observe changes to a given transaction signature."
) {

    private val api by requireObject<SolanaApi>()

    private val transactionSignature by argument(
        "--signature",
        help = "TRANSACTION_SIGNATURE"
    )

    private val commitment by commitmentOption()

    override fun run() = runBlocking {
        val subscription = api.createSubscription()
        subscription.connect()

        echo("Subscribed to changes for signature $transactionSignature")
        subscription.signatureSubscribe(transactionSignature, commitment)
            .take(1)
            .collect { signatureStatus ->
                echo(signatureStatus)
            }
    }
}