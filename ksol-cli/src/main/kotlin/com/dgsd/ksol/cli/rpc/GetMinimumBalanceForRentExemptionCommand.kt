package com.dgsd.ksol.cli.rpc

import com.dgsd.ksol.SolanaApi
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.long
import kotlinx.coroutines.runBlocking

class GetMinimumBalanceForRentExemptionCommand() : CliktCommand(
  name = "getMinimumBalanceForRentExemption"
) {

  private val api by requireObject<SolanaApi>()

  private val commitment by commitmentOption()
  private val accountDataLength by argument(
    name = "ACCOUNT_DATA_LENGTH"
  ).long()

  override fun run() = runBlocking {
    echo(api.getMinimumBalanceForRentExemption(accountDataLength, commitment))
  }
}