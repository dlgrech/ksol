package com.dgsd.android.solar.mobilewalletadapter.signmessages

import android.app.Application
import android.net.Uri
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.applock.biometrics.AppLockBiometricManager
import com.dgsd.android.solar.applock.biometrics.BiometricPromptResult
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.common.util.resourceFlowOf
import com.dgsd.android.solar.common.util.stateFlowOf
import com.dgsd.android.solar.extensions.append
import com.dgsd.android.solar.extensions.getString
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.android.solar.session.model.KeyPairSession
import com.dgsd.ksol.core.LocalTransactions
import com.dgsd.ksol.core.model.KeyPair
import com.solana.mobilewalletadapter.walletlib.scenario.SignMessagesRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

class MobileWalletAdapterSignMessagesViewModel(
  application: Application,
  private val signMessagesRequest: SignMessagesRequest,
  private val sessionManager: SessionManager,
  private val biometricManager: AppLockBiometricManager,
) : AndroidViewModel(application) {

  private val signMessagesResourceConsumer =
    ResourceFlowConsumer<Array<ByteArray>>(viewModelScope)

  val requesterName = stateFlowOf {
    signMessagesRequest.identityName
      ?: getString(R.string.mobile_wallet_adapter_unknown_requester)
  }

  val requesterIconUrl = stateFlowOf {
    val base = signMessagesRequest.identityUri
    val iconPath = signMessagesRequest.iconRelativeUri

    if (base == null || iconPath == null) {
      null
    } else {
      Uri.withAppendedPath(base, iconPath.encodedPath).toString()
    }
  }

  val requestUrl = stateFlowOf {
    signMessagesRequest.identityUri?.toString()
  }

  val messageCount = stateFlowOf {
    signMessagesRequest.payloads.size
  }

  private val isShowingBiometricPrompt = MutableStateFlow(false)

  private val _showBiometricAuthenticationPrompt = MutableEventFlow<BiometricPrompt.PromptInfo>()
  val showBiometricAuthenticationPrompt = _showBiometricAuthenticationPrompt.asEventFlow()

  val showSigningLoadingIndicator = signMessagesResourceConsumer.isLoadingOrError

  fun onCreate() {
    onEach(signMessagesResourceConsumer.data.filterNotNull()) {
      signMessagesRequest.completeWithSignedPayloads(it)
    }

    onEach(signMessagesResourceConsumer.error.filterNotNull()) {
      println("HERE: FUCK!")
      it.printStackTrace()
      signMessagesRequest.completeWithDecline()
    }
  }

  fun onSignClicked() {
    if (biometricManager.isAvailableOnDevice()) {
      isShowingBiometricPrompt.value = true
      _showBiometricAuthenticationPrompt.tryEmit(
        biometricManager.createPrompt(
          title = getString(R.string.send_unlock_biometrics_title),
          description = getString(R.string.send_unlock_biometrics_description)
        )
      )
    } else {
      upgradeSessionAndSignTransactions()
    }
  }

  fun onDeclineClicked() {
    signMessagesRequest.completeWithDecline()
  }

  fun onBiometricPromptResult(result: BiometricPromptResult) {
    isShowingBiometricPrompt.value = false
    if (result == BiometricPromptResult.SUCCESS) {
      upgradeSessionAndSignTransactions()
    } else if (result == BiometricPromptResult.FAIL) {
      signMessagesRequest.completeWithAuthorizationNotValid()
    }
  }

  private fun upgradeSessionAndSignTransactions() {
    sessionManager.upgradeSession()
    val session = sessionManager.activeSession.value
    if (session is KeyPairSession) {
      signTransactions(session.keyPair)
    } else {
      signMessagesRequest.completeWithDecline()
    }
  }

  private fun signTransactions(keyPair: KeyPair) {
    signMessagesResourceConsumer.collectFlow(
      resourceFlowOf {
        signMessagesRequest.payloads.map { payload ->
          val signature = LocalTransactions.sign(payload, keyPair)
          payload.append(signature)
        }.toTypedArray()
      }
    )
  }
}