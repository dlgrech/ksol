package com.dgsd.ksol.cli.rpc

import com.dgsd.ksol.SolanaApi
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import kotlinx.coroutines.runBlocking

class AccountSubscribeCommand() : CliktCommand(
  name = "accountSubscribe",
  help = "Observe changes to a given account address. Will run indefinitely monitoring updates"
) {

  private val api by requireObject<SolanaApi>()

  private val account by accountArgument()
  private val commitment by commitmentOption()

  override fun run() = runBlocking {
    val subscription = api.createSubscription()
    subscription.connect()

    echo("Subscribed to changes for account $account")
    subscription.accountSubscribe(account, commitment).collect { account ->
      echo(account)
    }
  }
}