package com.dgsd.ksol.cli

import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.model.AccountCirculatingStatus
import com.dgsd.ksol.model.Cluster
import com.dgsd.ksol.model.Commitment
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import java.time.Duration

private val NETWORKING_TIMEOUT = Duration.ofSeconds(30L)

fun main(arguments: Array<String>) {
    runBlocking {
        val api = SolanaApi(
            Cluster.MAINNET,
            OkHttpClient.Builder().readTimeout(NETWORKING_TIMEOUT).build()
        )

        launch {
            val balance = api.getBalance("FbGeZS8LiPCZiFpFwdUUeF2yxXtSsdfJoHTsVMvM8STh")
            println("Got balance: $balance")
        }

        launch {
            val blockTime = api.getBlockTime(103517167)
            println("Got block time: $blockTime")
        }

        launch {
            val blockHash = api.getRecentBlockhash()
            println("Got block hash: $blockHash")
        }

        launch {
            val blockHeight = api.getBlockHeight()
            println("Got block height: $blockHeight")
        }

        launch {
            val largestAccounts = api.getLargestAccounts(
                commitment = Commitment.CONFIRMED,
                circulatingStatus = AccountCirculatingStatus.NON_CIRCULATING
            )
            println("Got largest accounts: $largestAccounts")
        }
    }
}