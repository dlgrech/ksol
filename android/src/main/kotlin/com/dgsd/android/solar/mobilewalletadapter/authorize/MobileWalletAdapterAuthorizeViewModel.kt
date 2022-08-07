package com.dgsd.android.solar.mobilewalletadapter.authorize

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.dgsd.android.solar.R
import com.dgsd.android.solar.cluster.manager.ClusterManager
import com.dgsd.android.solar.common.util.stateFlowOf
import com.dgsd.android.solar.extensions.getString
import com.dgsd.android.solar.session.model.WalletSession
import com.dgsd.ksol.model.Cluster
import com.solana.mobilewalletadapter.walletlib.scenario.AuthorizeRequest

class MobileWalletAdapterAuthorizeViewModel(
  application: Application,
  private val callingPackage: String?,
  private val authorizeRequest: AuthorizeRequest,
  private val session: WalletSession,
  private val clusterManager: ClusterManager,
) : AndroidViewModel(application) {

  val requesterName = stateFlowOf {
    authorizeRequest.identityName
      ?: getString(R.string.mobile_wallet_adapter_authorize_unknown_requester)
  }

  val requesterIconUrl = stateFlowOf {
    val base = authorizeRequest.identityUri
    val iconPath = authorizeRequest.iconRelativeUri

    if (base == null || iconPath == null) {
      null
    } else {
      Uri.withAppendedPath(base, iconPath.encodedPath).toString()
    }
  }

  val requestUrl = stateFlowOf {
    authorizeRequest.identityUri?.toString()
  }

  fun onCreate() {
    val cluster = AuthRequestCluster.fromClusterName(authorizeRequest.cluster)?.toCluster()
    if (cluster != clusterManager.activeCluster.value) {
      // Mismatched clusters..
      authorizeRequest.completeWithDecline()
    } else if (callingPackage == null) {
      // Must have been from a web browser. We only su
    }
  }

  fun onApproveClicked() {
    authorizeRequest.completeWithAuthorize(
      session.publicKey.key,
      null,
      null,
      null
    )
  }

  fun onDeclineClicked() {
    authorizeRequest.completeWithDecline()
  }

  private enum class AuthRequestCluster(val clusterName: String) {
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
      fun fromClusterName(clusterName: String): AuthRequestCluster? {
        return values().firstOrNull { it.clusterName == clusterName }
      }
    }
  }
}