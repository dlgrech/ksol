package com.dgsd.ksol.cli.rpc

import com.dgsd.ksol.model.Commitment
import com.dgsd.ksol.model.PublicKey
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.*
import com.github.ajalt.clikt.parameters.options.OptionDelegate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice

fun CliktCommand.commitmentOption(): OptionDelegate<Commitment> {
    return option(
        "--commitment",
        help = "How finalized a block is at a point in time"
    ).choice(
        "finalized" to Commitment.FINALIZED,
        "confirmed" to Commitment.CONFIRMED,
        "processed" to Commitment.PROCESSED,
    ).default(
        Commitment.FINALIZED
    )
}

fun CliktCommand.accountArgument(): ArgumentDelegate<PublicKey> {
    return argument(
        name = "ACCOUNT",
        help = "Base58 hash of account"
    ).transformAll {
        PublicKey.fromBase58(it.single())
    }
}
fun CliktCommand.multipleAccounts(): ArgumentDelegate<List<PublicKey>> {
    return argument(
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
}