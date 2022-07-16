package com.dgsd.ksol.cli.keygen

import com.dgsd.ksol.keygen.KeyFactory
import com.github.ajalt.clikt.core.CliktCommand
import kotlinx.coroutines.runBlocking

class GenerateSeedCommand : CliktCommand(
  name = "seed",
  help = "Generate key pair from mnemonic"
) {

  private val passPhase by passPhraseOption()
  private val mnemonic by mnemonicArgument()

  override fun run() = runBlocking {
    val seed = KeyFactory.createSeedFromMnemonic(mnemonic, passPhase)
    val keyPair = KeyFactory.createKeyPairFromSeed(seed)
    echo(
      "publicKey: ${keyPair.publicKey.toBase58String()} " +
          "/ privateKey: ${keyPair.privateKey.toBase58String()}"
    )
  }
}