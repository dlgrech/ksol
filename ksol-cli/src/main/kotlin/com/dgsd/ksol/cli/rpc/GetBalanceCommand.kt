package com.dgsd.ksol.cli.rpc

import com.dgsd.ksol.SolanaApi
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import kotlinx.coroutines.runBlocking

class GetBalanceCommand() : CliktCommand(
  name = "getBalance"
) {

  private val api by requireObject<SolanaApi>()

  private val account by accountArgument()
  private val commitment by commitmentOption()

  override fun run() = runBlocking {
    echo(api.getBalance(account, commitment))
  }
}