package com.dgsd.ksol.cli

import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.model.Cluster
import com.dgsd.ksol.model.Commitment
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import java.time.Duration

private val NETWORKING_TIMEOUT = Duration.ofSeconds(30L)

class RpcCommand : CliktCommand(
    help = "Execute Solana JSON RPC methods"
) {

    private val cluster by requireObject<Cluster>()

    private val rpcCommandName by argument(
        name = "method",
        help = "JSONRPC API method name",
    )

    private val commitment by option(
        "-c",
        "--commitment",
        help = "How finalized a block is at a point in time"
    ).choice(
        "finalized" to Commitment.FINALIZED,
        "confirmed" to Commitment.CONFIRMED,
        "processed" to Commitment.PROCESSED,
    ).default(
        Commitment.FINALIZED
    )

    override fun run() = runBlocking {
        val api = SolanaApi(cluster, OkHttpClient.Builder()
            .connectTimeout(NETWORKING_TIMEOUT)
            .readTimeout(NETWORKING_TIMEOUT)
            .build())

        when (rpcCommandName) {
            "getBlockHeight" -> {
                echo(api.getBlockHeight(commitment))
            }
            "getRecentBlockhash" -> {
                echo(api.getRecentBlockhash(commitment))
            }
            "getSupply" -> {
                echo(api.getSupply(commitment))
            }
            "getTransactionCount" -> {
                echo(api.getTransactionCount(commitment))
            }
            else -> throw PrintMessage("Unknown/unimplemented RPC command: $rpcCommandName", error = true)
        }
    }
}