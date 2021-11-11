package com.dgsd.ksol.model

sealed class Cluster(
    url: String,
) {
    val rpcUrl = "https://$url"

    val webSocketUrl = "ws://$url"

    object DEVNET : Cluster(url = "api.devnet.solana.com")

    object TESTNET : Cluster(url = "api.testnet.solana.com")

    object MAINNET : Cluster(url = "api.mainnet-beta.solana.com")

    class Custom(endpoint: String) : Cluster(endpoint)
}
