package com.dgsd.ksol.cli.rpc

import com.dgsd.ksol.SolanaApi
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.long
import kotlinx.coroutines.runBlocking

class GetBlockTimeCommand() : CliktCommand(
  name = "getBlockTime"
) {

  private val api by requireObject<SolanaApi>()

  private val slotNumber by argument(
    name = "SLOT_NUMBER"
  ).long()

  override fun run() = runBlocking {
    echo(api.getBlockTime(slotNumber))
  }
}