package com.dgsd.ksol.cli.rpc

import com.dgsd.ksol.SolanaApi
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import kotlinx.coroutines.runBlocking

class GetTransactionCommand() : CliktCommand(
  name = "getTransaction"
) {

  private val api by requireObject<SolanaApi>()

  private val commitment by commitmentOption()
  private val transaction by argument(
    name = "TRANSACTION"
  )

  override fun run() = runBlocking {
    echo(api.getTransaction(transaction, commitment))
  }
}