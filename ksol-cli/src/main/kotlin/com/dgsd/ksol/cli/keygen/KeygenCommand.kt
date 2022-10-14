package com.dgsd.ksol.cli.keygen

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class KeygenCommand private constructor() : CliktCommand(
    help = "Operations relating to Solana public/private keys"
) {

    override fun run() = Unit

    companion object {

        fun create(): KeygenCommand {
            return KeygenCommand()
                .subcommands(
                    GenerateAccountsCommand(),
                    GenerateKeyPairCommand(),
                    GenerateMnemonicCommand(),
                    GenerateSeedCommand(),
                )
        }
    }
}