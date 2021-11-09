package com.dgsd.ksol.cli.keygen

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.ArgumentDelegate
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.validate
import com.github.ajalt.clikt.parameters.options.OptionDelegate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option

fun CliktCommand.passPhraseOption(): OptionDelegate<String> {
    return option("--passphrase").default("")
}

fun CliktCommand.mnemonicArgument(): ArgumentDelegate<List<String>> {
    return argument(
        name = "MNEMONIC",
        help = "12 or 24 words used to generate a keypair"
    ).multiple(required = true).validate {
        kotlin.require(it.size == 12 || it.size == 24) {
            "MMnemonic must be 12 or 24 words"
        }
    }
}
