package com.dgsd.android.solar.mobilewalletadapter.signtransactions

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.common.util.resourceFlowOf
import com.dgsd.android.solar.common.util.stateFlowOf
import com.dgsd.android.solar.extensions.getString
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.ksol.LocalTransactions
import com.dgsd.ksol.model.LocalTransaction
import com.solana.mobilewalletadapter.walletlib.scenario.SignTransactionsRequest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class MobileWalletAdapterSignTransactionViewModel(
  application: Application,
  private val signTransactionsRequest: SignTransactionsRequest,
  private val sessionManager: SessionManager,
) : AndroidViewModel(application) {

  private val deserializeTransactionsResourceConsumer =
    ResourceFlowConsumer<List<LocalTransaction>>(viewModelScope)

  val requesterName = stateFlowOf {
    signTransactionsRequest.identityName
      ?: getString(R.string.mobile_wallet_adapter_unknown_requester)
  }

  val requesterIconUrl = stateFlowOf {
    val base = signTransactionsRequest.identityUri
    val iconPath = signTransactionsRequest.iconRelativeUri

    if (base == null || iconPath == null) {
      null
    } else {
      Uri.withAppendedPath(base, iconPath.encodedPath).toString()
    }
  }

  val requestUrl = stateFlowOf {
    signTransactionsRequest.identityUri?.toString()
  }

  val isAuthorizationButtonsVisible = deserializeTransactionsResourceConsumer.data.map {
    !it.isNullOrEmpty()
  }

  fun onCreate() {
    deserializeTransactionsResourceConsumer.collectFlow(
      resourceFlowOf {
        signTransactionsRequest.payloads.map { LocalTransactions.deserializeTransaction(it) }
      }
    )

    onEach(deserializeTransactionsResourceConsumer.error.filterNotNull()) {
      signTransactionsRequest.completeWithDecline()
    }
  }

  fun onSignClicked() {

  }

  fun onDeclineClicked() {

  }
}