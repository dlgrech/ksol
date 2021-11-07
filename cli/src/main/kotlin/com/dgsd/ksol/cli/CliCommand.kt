package com.dgsd.ksol.cli

import com.dgsd.ksol.model.Cluster
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice

class CliCommand : CliktCommand(
    name = "ksol",
    help = "Interact with the ksol Solana library"
) {

    private val cluster by option(
        "-c",
        "--cluster",
        help = "The cluster to use when running operations",
    ).choice(
        "mainnet" to Cluster.MAINNET,
        "testnet" to Cluster.TESTNET,
        "devnet" to Cluster.DEVNET,
    ).default(
        Cluster.MAINNET
    )

    override fun run() {
        currentContext.obj = cluster
    }
}