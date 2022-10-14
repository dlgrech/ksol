package com.dgsd.ksol.cli

import com.github.ajalt.clikt.core.CliktCommand

class CliCommand : CliktCommand(
  name = "ksol",
  help = "Interact with the ksol Solana library"
) {
  override fun run() = Unit
}