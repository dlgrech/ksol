package com.dgsd.ksol.cli.keygen

import com.dgsd.ksol.keygen.KeyFactory
import com.github.ajalt.clikt.core.CliktCommand

class GenerateAccountsCommand : CliktCommand(
    name = "accounts",
    help = "Generate multiple account key pair from mnemonic"
) {

    private val passPhase by passPhraseOption()
    private val mnemonic by mnemonicOption()

    override fun run() {
        for (i in 0..20) {
            val keyPair = KeyFactory.createKeyPairFromMnemonic(mnemonic, passPhase, i)
            echo("account index #$i: $keyPair")
        }
    }

}