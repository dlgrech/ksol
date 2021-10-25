package com.dgsd.ksol.cli

import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.model.Cluster
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(arguments: Array<String>) {
    runBlocking {
        val api = SolanaApi(Cluster.MAINNET)

        launch {
            val blockTime = api.getBlockTime(103517167)
            println("Got block time: $blockTime")
        }

        launch {
            val blockhash = api.getRecentBlockhash()
            println("Got block hash: $blockhash")
        }
    }
}