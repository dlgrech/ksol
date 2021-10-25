package com.dgsd.ksol.cli

import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.model.Cluster
import kotlinx.coroutines.runBlocking

fun main(arguments: Array<String>) = runBlocking {
    val api = SolanaApi(Cluster.MAINNET)

    val blockhash = api.getRecentBlockhash()

    println("Got Blockhash: $blockhash")
}