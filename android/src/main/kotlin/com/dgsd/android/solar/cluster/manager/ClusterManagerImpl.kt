package com.dgsd.android.solar.cluster.manager

import android.content.SharedPreferences
import androidx.core.content.edit
import com.dgsd.ksol.core.model.Cluster
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val PREF_KEY_CLUSTER_RPC_URL = "active_cluster_rpc_url"
private const val PREF_KEY_CLUSTER_WS_URL = "active_cluster_ws_url"

class ClusterManagerImpl(
    private val sharedPreferences: SharedPreferences,
    defaultCluster: Cluster
) : ClusterManager {

    private val _activeCluster = MutableStateFlow(defaultCluster)
    override val activeCluster = _activeCluster.asStateFlow()

    init {
        val rpcUrl = sharedPreferences.getString(PREF_KEY_CLUSTER_RPC_URL, null)
        val wsUrl = sharedPreferences.getString(PREF_KEY_CLUSTER_WS_URL, null)

        if (rpcUrl != null && wsUrl != null) {
            setCluster(createCluster(rpcUrl, wsUrl))
        }
    }

    override fun setCluster(cluster: Cluster) {
        _activeCluster.value = cluster
        persistCluster(cluster)
    }

    private fun createCluster(rpcUrl: String, wsUrl: String): Cluster {
        return knownClusters.firstOrNull { cluster ->
            cluster.rpcUrl == rpcUrl && cluster.webSocketUrl == wsUrl
        } ?: Cluster.Custom(rpcUrl, wsUrl)
    }

    private fun persistCluster(cluster: Cluster) {
        sharedPreferences.edit {
            putString(PREF_KEY_CLUSTER_RPC_URL, cluster.rpcUrl)
            putString(PREF_KEY_CLUSTER_WS_URL, cluster.webSocketUrl)
        }
    }

    companion object {
        private val knownClusters = arrayOf(Cluster.DEVNET, Cluster.TESTNET, Cluster.MAINNET_BETA)
    }
}