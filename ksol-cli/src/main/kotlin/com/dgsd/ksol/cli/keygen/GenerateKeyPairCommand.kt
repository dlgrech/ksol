package com.dgsd.ksol.cli.keygen

import com.dgsd.ksol.keygen.KeyFactory
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import kotlinx.coroutines.runBlocking

class GenerateKeyPairCommand : CliktCommand(
  name = "keypair",
  help = "Generate account key pair from mnemonic"
) {

  private val passPhase by passPhraseOption()
  private val mnemonic by mnemonicArgument()

  private val accountIndex by option(
    "--account"
  ).int().default(0)

  override fun run() = runBlocking {
    val keyPair = KeyFactory.createKeyPairFromMnemonic(mnemonic, passPhase, accountIndex)
    echo(
      "public = ${keyPair.publicKey.toBase58String()} /" +
        " private = ${keyPair.privateKey.toBase58String()}"
    )
  }

}