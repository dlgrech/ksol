package com.dgsd.ksol.cli.keygen

import com.dgsd.ksol.keygen.KeyFactory
import com.dgsd.ksol.keygen.MnemonicPhraseLength
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import kotlinx.coroutines.runBlocking

class GenerateMnemonicCommand : CliktCommand(
    name = "mnemonic",
    help = "Generate a new mnemonic phrase"
) {

    private val phraseLength by option(
        "--length"
    ).choice(
        "12" to MnemonicPhraseLength.TWELVE,
        "24" to MnemonicPhraseLength.TWENTY_FOUR
    ).default(MnemonicPhraseLength.TWENTY_FOUR)

    override fun run() = runBlocking {
        val mnemonic = KeyFactory.createMnemonic(phraseLength)
        echo(mnemonic)
    }
}