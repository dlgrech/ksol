package com.dgsd.ksol.cli.send

import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.Transactions
import com.dgsd.ksol.cli.util.clusterOption
import com.dgsd.ksol.keygen.KeyFactory
import com.dgsd.ksol.model.Commitment
import com.dgsd.ksol.model.PrivateKey
import com.dgsd.ksol.model.PublicKey
import com.dgsd.ksol.model.TransactionSignatureStatus
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.transformAll
import com.github.ajalt.clikt.parameters.types.long
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient

class SendCommand : CliktCommand(
    name = "send",
    help = "Send a transaction"
) {

    private val cluster by clusterOption()

    private val fromAccount by argument(
        name = "SENDER",
        help = "Base58 hash of private key to send from"
    ).transformAll {
        PrivateKey.fromBase58(it.single())
    }

    private val toAccount by argument(
        name = "RECEIVER",
        help = "Base58 hash of account to send to"
    ).transformAll {
        PublicKey.fromBase58(it.single())
    }

    private val lamports by argument(
        name = "LAMPORTS",
        help = "The amount to send, in lamports"
    ).long()

    override fun run() = runBlocking {
        val httpClient = OkHttpClient.Builder().build()

        val api = SolanaApi(cluster, httpClient)

        val recentBlockhash = api.getRecentBlockhash().blockhash

        echo("Using recent blockhash: $recentBlockhash")

        val keyPair = KeyFactory.createKeyPairFromPrivateKey(fromAccount)

        echo("Sending from: ${keyPair.publicKey}")

        val transactionSignature = api.sendTransaction(
            Transactions.createTransferTransaction(
                keyPair,
                toAccount,
                lamports,
                recentBlockhash
            )
        )

        echo("Transaction signature: $transactionSignature")

        var isConfirmed = false
        var attempts = 0
        while (attempts < 5) {
            echo("Waiting for confirmation..")
            val status = api.getSignatureStatuses(listOf(transactionSignature)).first()
            if (status is TransactionSignatureStatus.Confirmed) {
                echo("Got signature status: $status")
                isConfirmed =
                    status.commitment == Commitment.CONFIRMED || status.commitment == Commitment.FINALIZED
                break
            } else {
                delay(3000)
            }
        }

        if (!isConfirmed) {
            echo("Could not confirm transaction")
        } else {
            val transaction = api.getTransaction(transactionSignature, Commitment.CONFIRMED)
            echo("Got transaction = $transaction")
        }
    }
}