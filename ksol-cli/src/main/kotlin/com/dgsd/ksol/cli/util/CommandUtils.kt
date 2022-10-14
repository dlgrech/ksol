package com.dgsd.ksol.cli.util

import com.dgsd.ksol.core.model.Cluster
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.OptionDelegate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice

fun CliktCommand.clusterOption(): OptionDelegate<Cluster> {
    return option(
        "--cluster",
        help = "The cluster to use when running operations",
    ).choice(
        "mainnet" to Cluster.MAINNET_BETA,
        "testnet" to Cluster.TESTNET,
        "devnet" to Cluster.DEVNET,
    ).default(
        Cluster.MAINNET_BETA
    )
}