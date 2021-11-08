package com.dgsd.ksol.cli

import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.model.*
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.MissingOption
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.long
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import java.time.Duration

private val NETWORKING_TIMEOUT = Duration.ofSeconds(60L)

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

    private val accountOption = option(
        "--account",
        help = "Passed as an argument to RPC calls that require a Base58 hash of an account"
    )
    private val account by accountOption

    private val transactionSignatureOption = option(
        "--transaction",
        help = "Passed as an argument to RPC calls that require a Base58 transaction signature of an account"
    )
    private val transactionSignature by transactionSignatureOption

    private val lamportsOption = option(
        "--lamports",
        help = "Passed as an argument to RPC calls that require a lamport-amount input"
    ).long()
    private val lamports by lamportsOption

    private val slotNumberOption = option(
        "--slot",
        help = "Passed as an argument to RPC calls that require a slot number input"
    ).long()
    private val slotNumber by slotNumberOption

    private val accountDataLengthOption = option(
        "--account-data-length",
        help = "Passed as an argument to RPC calls that require an account data length input"
    ).long()
    private val accountDataLength by accountDataLengthOption

    private val circulatingStatusOption = option(
        "--circulating-status",
        help = "Passed as an argument to RPC calls that require a circulating status input"
    ).choice(
        "circulating" to AccountCirculatingStatus.CIRCULATING,
        "non-circulating" to AccountCirculatingStatus.NON_CIRCULATING
    )
    private val circulatingStatus by circulatingStatusOption

    override fun run() = runBlocking {
        val api = SolanaApi(cluster, OkHttpClient.Builder()
            .connectTimeout(NETWORKING_TIMEOUT)
            .readTimeout(NETWORKING_TIMEOUT)
            .build())

        when (rpcCommandName) {
            "getAccountInfo" -> {
                echo(api.getAccountInfo(ensureAccount(), commitment))
            }
            "getBalance" -> {
                echo(api.getBalance(ensureAccount(), commitment))
            }
            "getBlockHeight" -> {
                echo(api.getBlockHeight(commitment))
            }
            "getBlockTime" -> {
                echo(api.getBlockTime(ensureSlotNumber()))
            }
            "getLargestAccounts" -> {
                echo(api.getLargestAccounts(circulatingStatus, commitment))
            }
            "getMinimumBalanceForRentExemption" -> {
                echo(api.getMinimumBalanceForRentExemption(ensureAccountDataLength(), commitment))
            }
            "getRecentBlockhash" -> {
                echo(api.getRecentBlockhash(commitment))
            }
            "getProgramAccounts" -> {
                echo(api.getProgramAccounts(ensureAccount(), commitment))
            }
            "getSupply" -> {
                echo(api.getSupply(commitment))
            }
            "getTransaction" -> {
                echo(api.getTransaction(ensureTransactionSignature(), commitment))
            }
            "getTransactionCount" -> {
                echo(api.getTransactionCount(commitment))
            }
            "requestAirdrop" -> {
                echo(api.requestAirdrop(ensureAccount(), ensureLamports(), commitment))
            }
            else -> throw PrintMessage("Unknown/unimplemented RPC command: $rpcCommandName", error = true)
        }
    }

    private fun ensureAccount(): PublicKey {
        val account = account
        return if (account == null) {
            throw MissingOption(accountOption)
        } else {
            PublicKey.fromBase58(account)
        }
    }

    private fun ensureTransactionSignature(): TransactionSignature {
        return transactionSignature ?: throw MissingOption(transactionSignatureOption)
    }

    private fun ensureLamports(): Long {
        return lamports ?: throw MissingOption(lamportsOption)
    }

    private fun ensureSlotNumber(): Long {
        return slotNumber ?: throw MissingOption(slotNumberOption)
    }

    private fun ensureAccountDataLength(): Long {
        return accountDataLength ?: throw MissingOption(accountDataLengthOption)
    }
}