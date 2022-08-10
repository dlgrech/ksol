package com.dgsd.ksol.model

sealed class Cluster(
    val rpcUrl: String,
    val webSocketUrl: String,
) {

    constructor(url: String) : this(
        rpcUrl = "https://$url",
        webSocketUrl = "ws://$url"
    )

    object DEVNET : Cluster(url = "api.devnet.solana.com")

    object TESTNET : Cluster(url = "api.testnet.solana.com")

    object MAINNET_BETA : Cluster(url = "api.mainnet-beta.solana.com")

    class Custom(rpcUrl: String, webSocketUrl: String) : Cluster(rpcUrl, webSocketUrl)
}
