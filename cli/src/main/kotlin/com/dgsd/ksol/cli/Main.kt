package com.dgsd.ksol.cli

import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.model.Cluster

fun main(arguments: Array<String>) {
    val api = SolanaApi(Cluster.MAINNET)
}