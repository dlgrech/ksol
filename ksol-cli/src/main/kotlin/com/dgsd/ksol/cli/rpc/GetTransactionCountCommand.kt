package com.dgsd.ksol.cli.rpc

import com.dgsd.ksol.SolanaApi
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import kotlinx.coroutines.runBlocking

class GetTransactionCountCommand() : CliktCommand(
  name = "getTransactionCount"
) {

  private val api by requireObject<SolanaApi>()

  private val commitment by commitmentOption()

  override fun run() = runBlocking {
    echo(api.getTransactionCount(commitment))
  }
}