package com.dgsd.ksol.model

sealed class Cluster(
    val endpoint: String
) {

    object DEVNET : Cluster(endpoint = "https://api.devnet.solana.com")

    object TESTNET : Cluster(endpoint = "https://api.testnet.solana.com")

    object MAINNET : Cluster(endpoint = "https://api.mainnet-beta.solana.com")

    class Custom(endpoint: String) : Cluster(endpoint)
}
