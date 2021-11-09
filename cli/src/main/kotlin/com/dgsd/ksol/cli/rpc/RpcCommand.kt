package com.dgsd.ksol.cli.rpc

import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.model.Cluster
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import okhttp3.OkHttpClient
import java.time.Duration

private val NETWORKING_TIMEOUT = Duration.ofSeconds(60L)

class RpcCommand private constructor() : CliktCommand(
    help = "Execute Solana JSON RPC methods"
) {

    private val cluster by option(
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
        val api = SolanaApi(cluster, OkHttpClient.Builder()
            .connectTimeout(NETWORKING_TIMEOUT)
            .readTimeout(NETWORKING_TIMEOUT)
            .build())

        currentContext.obj = api
    }

    companion object {

        fun create(): RpcCommand {
            return RpcCommand()
                .subcommands(
                    GetAccountInfoCommand(),
                    GetBalanceCommand(),
                    GetBlockHeightCommand(),
                    GetBlockTimeCommand(),
                    GetLargestAccountsCommand(),
                    GetMinimumBalanceForRentExemptionCommand(),
                    GetMultipleAccountsCommand(),
                    GetProgramAccountsCommand(),
                    GetRecentBlockhashCommand(),
                    GetSignaturesForAddressCommand(),
                    GetSignatureStatusesCommand(),
                    GetSupplyCommand(),
                    GetTransactionCommand(),
                    GetTransactionCountCommand(),
                    RequestAirdropCommand()
                )
        }
    }
}