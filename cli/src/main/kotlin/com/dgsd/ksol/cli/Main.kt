package com.dgsd.ksol.cli

import com.github.ajalt.clikt.core.subcommands

fun main(arguments: Array<String>) =
    CliCommand()
        .subcommands(RpcCommand())
        .main(arguments)