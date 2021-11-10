package com.dgsd.ksol.cli

import com.dgsd.ksol.cli.keygen.KeygenCommand
import com.dgsd.ksol.cli.rpc.RpcCommand
import com.dgsd.ksol.cli.send.SendCommand
import com.github.ajalt.clikt.core.subcommands
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlin.system.exitProcess

fun main(arguments: Array<String>) {
    runBlocking {
        supervisorScope {
            try {
                CliCommand()
                    .subcommands(
                        RpcCommand.create(),
                        KeygenCommand.create(),
                        SendCommand()
                    )
                    .main(arguments)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    exitProcess(0)
}
