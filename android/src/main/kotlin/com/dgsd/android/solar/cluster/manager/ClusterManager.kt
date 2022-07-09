package com.dgsd.android.solar.cluster.manager

import com.dgsd.ksol.model.Cluster
import kotlinx.coroutines.flow.StateFlow

interface ClusterManager {

    val activeCluster: StateFlow<Cluster>

    fun setCluster(cluster: Cluster)
}