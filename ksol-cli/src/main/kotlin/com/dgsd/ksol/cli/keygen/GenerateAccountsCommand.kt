package com.dgsd.ksol.cli.keygen

import com.dgsd.ksol.keygen.KeyFactory
import com.github.ajalt.clikt.core.CliktCommand
import kotlinx.coroutines.runBlocking

class GenerateAccountsCommand : CliktCommand(
  name = "accounts",
  help = "Generate multiple account key pair from mnemonic"
) {

  private val passPhase by passPhraseOption()
  private val mnemonic by mnemonicArgument()

  override fun run() = runBlocking {
    for (i in 0..20) {
      val keyPair = KeyFactory.createKeyPairFromMnemonic(mnemonic, passPhase, i)
      echo(
        "account index #$i: " +
          "public = ${keyPair.publicKey.toBase58String()} / " +
          "private = ${keyPair.privateKey.toBase58String()}"
      )
    }
  }

}