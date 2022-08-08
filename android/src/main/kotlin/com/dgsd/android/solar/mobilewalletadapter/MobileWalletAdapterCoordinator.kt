package com.dgsd.android.solar.mobilewalletadapter

import com.dgsd.android.solar.cluster.manager.ClusterManager
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.SimpleMutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.flow.call
import com.dgsd.android.solar.mobilewalletadapter.model.MobileWalletAuthRequestCluster
import com.solana.mobilewalletadapter.walletlib.scenario.AuthorizeRequest
import com.solana.mobilewalletadapter.walletlib.scenario.ReauthorizeRequest
import com.solana.mobilewalletadapter.walletlib.scenario.Scenario

class MobileWalletAdapterCoordinator internal constructor(
  private val clusterManager: ClusterManager,
  private val authorityManager: MobileWalletAdapterAuthorityManager,
  val callingPackage: String?,
  private val scenario: Scenario,
) {

  sealed interface Destination {
    data class Authorize(val request: AuthorizeRequest) : Destination
  }

  private val _destination = MutableEventFlow<Destination>()
  val destination = _destination.asEventFlow()

  private val _terminate = SimpleMutableEventFlow()
  val terminate = _terminate.asEventFlow()

  var authorizationRequest: AuthorizeRequest? = null
    private set

  fun start() {
    scenario.start()
  }

  fun close() {
    scenario.close()
  }

  internal fun navigateToAuthorizationRequest(request: AuthorizeRequest) {
    if (
      clusterManager.activeCluster.value !=
      MobileWalletAuthRequestCluster.fromClusterName(request.cluster).toCluster()
    ) {
      // Mismatched cluster
      request.completeWithDecline()
      _terminate.call()
    } else {
      authorizationRequest = request
      _destination.tryEmit(Destination.Authorize(request))
    }
  }

  internal fun navigateWithReauthorizationRequest(request: ReauthorizeRequest) {
    if (
      clusterManager.activeCluster.value !=
      MobileWalletAuthRequestCluster.fromClusterName(request.cluster).toCluster()
    ) {
      // Mismatched cluster
      request.completeWithDecline()
      _terminate.call()
    } else {
      if (authorityManager.isValidAuthority(callingPackage, request.authorizationScope)) {
        request.completeWithReauthorize()
      } else {
        request.completeWithDecline()
      }

      _terminate.call()
    }
  }

  internal fun onTeardownComplete() {
    _terminate.call()
  }
}