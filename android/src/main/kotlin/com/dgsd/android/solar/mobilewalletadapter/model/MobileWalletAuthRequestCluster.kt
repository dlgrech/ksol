package com.dgsd.android.solar.mobilewalletadapter.model

import com.dgsd.ksol.model.Cluster

internal enum class MobileWalletAuthRequestCluster(val clusterName: String) {
  TESTNET("testnet"),
  DEVNET("devnet"),
  MAINNET_BETA("mainnet-beta")
  ;

  fun toCluster(): Cluster {
    return when (this) {
      TESTNET -> Cluster.TESTNET
      DEVNET -> Cluster.DEVNET
      MAINNET_BETA -> Cluster.MAINNET
    }
  }

  companion object {
    fun fromClusterName(clusterName: String?): MobileWalletAuthRequestCluster {
      return values().firstOrNull { it.clusterName == clusterName } ?: MAINNET_BETA
    }
  }
}