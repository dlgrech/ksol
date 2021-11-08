package com.dgsd.ksol.cli.keygen

import com.dgsd.ksol.keygen.KeyFactory
import com.github.ajalt.clikt.core.CliktCommand

class GenerateSeedCommand : CliktCommand(
    name = "seed",
    help = "Generate key pair from mnemonic"
) {

    private val passPhase by passPhraseOption()
    private val mnemonic by mnemonicOption()

    override fun run() {
        val seed = KeyFactory.createSeedFromMnemonic(mnemonic, passPhase)
        echo(KeyFactory.createKeyPairFromSeed(seed))
    }
}