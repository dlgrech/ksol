package com.dgsd.ksol.cli

import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.keygen.KeyFactory
import com.dgsd.ksol.model.AccountCirculatingStatus
import com.dgsd.ksol.model.Cluster
import com.dgsd.ksol.model.Commitment
import com.dgsd.ksol.model.PublicKey
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import java.time.Duration

private val NETWORKING_TIMEOUT = Duration.ofSeconds(30L)

fun main(arguments: Array<String>) {
    runBlocking {
        val passPhrase = ""
        val mnemonic = "sentence ugly section antenna motion bind adapt vault increase milk lawn humor".split(" ")

        for (i in 0..10) {
            println("account #$i: ${KeyFactory.createKeyPairFromMnemonic(mnemonic, passPhrase, i)}")
        }

        val api = SolanaApi(
            Cluster.MAINNET,
            OkHttpClient.Builder().readTimeout(NETWORKING_TIMEOUT).build()
        )

        launch {
            val accountInfo = api.getAccountInfo(
                PublicKey.fromBase58("FbGeZS8LiPCZiFpFwdUUeF2yxXtSsdfJoHTsVMvM8STh")
            )
            println("Got account info: $accountInfo")
        }

        launch {
            val balance = api.getBalance(
                PublicKey.fromBase58("FbGeZS8LiPCZiFpFwdUUeF2yxXtSsdfJoHTsVMvM8STh")
            )
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
            val programAccounts = api.getProgramAccounts(
                PublicKey.fromBase58("Config1111111111111111111111111111111111111")
            )
            println("Got program accounts: ${programAccounts.size}")
        }

        launch {
            val transactionCount = api.getTransactionCount()
            println("Got transaction count: $transactionCount")
        }

        launch {
            val supplySummary = api.getSupply()
            println("Got supply summary: $supplySummary")
        }

        launch {
            val minBalance = api.getMinimumBalanceForRentExemption(50)
            println("Got min balance for rent exemption: $minBalance")
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