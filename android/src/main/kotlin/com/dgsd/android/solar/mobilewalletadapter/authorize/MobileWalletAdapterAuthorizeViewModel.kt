package com.dgsd.android.solar.mobilewalletadapter.authorize

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.util.stateFlowOf
import com.dgsd.android.solar.extensions.getString
import com.dgsd.android.solar.mobilewalletadapter.MobileWalletAdapterAuthorityManager
import com.dgsd.android.solar.session.model.WalletSession
import com.solana.mobilewalletadapter.walletlib.scenario.AuthorizeRequest

class MobileWalletAdapterAuthorizeViewModel(
  application: Application,
  private val callingPackage: String,
  private val authorizeRequest: AuthorizeRequest,
  private val session: WalletSession,
  private val authorityManager: MobileWalletAdapterAuthorityManager,
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

  fun onApproveClicked() {
    authorizeRequest.completeWithAuthorize(
      session.publicKey.key,
      null,
      null,
      authorityManager.createAuthority(callingPackage)
    )
  }

  fun onDeclineClicked() {
    authorizeRequest.completeWithDecline()
  }
}