package com.dgsd.ksol.cli.rpc

import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.core.LocalTransactions
import com.dgsd.ksol.core.model.PrivateKey
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.keygen.KeyFactory
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.transformAll
import com.github.ajalt.clikt.parameters.types.long
import kotlinx.coroutines.runBlocking

class SendTransactionCommand() : CliktCommand(
  name = "sendTransaction"
) {

  private val api by requireObject<SolanaApi>()

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

  private val recentBlockhash by argument(
    name = "RECENT_BLOCKHASH",
  )

  override fun run() = runBlocking {
    val keyPair = KeyFactory.createKeyPairFromPrivateKey(fromAccount)

    echo(
      api.sendTransaction(
        LocalTransactions.createTransferTransaction(
          sender = keyPair,
          recipient = toAccount,
          lamports = lamports,
          memo = null,
          recentBlockhash = PublicKey.fromBase58(recentBlockhash)
        )
      )
    )
  }
}