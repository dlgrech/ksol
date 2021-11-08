package com.dgsd.ksol.cli

import com.dgsd.ksol.cli.keygen.KeygenCommand
import com.dgsd.ksol.cli.rpc.RpcCommand
import com.github.ajalt.clikt.core.subcommands

fun main(arguments: Array<String>) =
    CliCommand()
        .subcommands(
            RpcCommand.create(),
            KeygenCommand.create(),
        )
        .main(arguments)