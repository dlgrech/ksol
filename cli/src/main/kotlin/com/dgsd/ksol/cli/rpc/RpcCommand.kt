package com.dgsd.ksol.cli.rpc

import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.cli.util.clusterOption
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import okhttp3.OkHttpClient
import java.time.Duration

private val NETWORKING_TIMEOUT = Duration.ofSeconds(60L)

class RpcCommand private constructor() : CliktCommand(
    name = "rpc",
    help = "Execute Solana JSON RPC methods"
) {

    private val cluster by clusterOption()

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
                    AccountSubscribeCommand(),
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
                    RequestAirdropCommand(),
                    SendTransactionCommand(),
                )
        }
    }
}