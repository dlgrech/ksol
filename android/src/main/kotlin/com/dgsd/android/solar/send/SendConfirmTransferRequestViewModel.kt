package com.dgsd.android.solar.send

import android.app.Application
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.applock.biometrics.AppLockBiometricManager
import com.dgsd.android.solar.applock.biometrics.BiometricPromptResult
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.common.ui.PublicKeyFormatter
import com.dgsd.android.solar.common.ui.SolTokenFormatter
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.common.util.mapData
import com.dgsd.android.solar.common.util.stateFlowOf
import com.dgsd.android.solar.extensions.getString
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.repository.SolanaApiRepository
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.android.solar.session.model.KeyPairSession
import com.dgsd.ksol.core.model.KeyPair
import com.dgsd.ksol.core.model.Lamports
import com.dgsd.ksol.core.model.TransactionSignature
import com.dgsd.ksol.solpay.model.SolPayTransferRequest
import com.dgsd.ksol.core.utils.solToLamports
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull

class SendConfirmTransferRequestViewModel(
  application: Application,
  private val sessionManager: SessionManager,
  private val transferRequest: SolPayTransferRequest,
  private val publicKeyFormatter: PublicKeyFormatter,
  private val errorMessageFactory: ErrorMessageFactory,
  private val solanaApiRepository: SolanaApiRepository,
  private val biometricManager: AppLockBiometricManager,
) : AndroidViewModel(application) {

  private val submitTransactionResourceConsumer =
    ResourceFlowConsumer<TransactionSignature>(viewModelScope)

  private val getFeeResourceConsumer = ResourceFlowConsumer<Lamports>(viewModelScope)

  val recipientText = stateFlowOf {
    publicKeyFormatter.format(transferRequest.recipient)
  }

  val amountText = stateFlowOf {
    SolTokenFormatter.format(checkNotNull(transferRequest.amount?.solToLamports()))
  }

  val messageText = stateFlowOf { transferRequest.message }
  val labelText = stateFlowOf { transferRequest.label }
  val memoText = stateFlowOf { transferRequest.memo }

  val isLoadingFee = getFeeResourceConsumer.isLoading

  val feeText = combine(getFeeResourceConsumer.data, getFeeResourceConsumer.error) { fee, error ->
    if (fee != null) {
      SolTokenFormatter.format(fee)
    } else if (error != null) {
      getString(R.string.send_transfer_request_confirmation_error_getting_fee)
    } else {
      null
    }
  }

  private val _showBiometricAuthenticationPrompt = MutableEventFlow<BiometricPrompt.PromptInfo>()
  val showBiometricAuthenticationPrompt = _showBiometricAuthenticationPrompt.asEventFlow()

  private val _showError = MutableEventFlow<CharSequence>()
  val showError = _showError.asEventFlow()

  private val isShowingBiometricPrompt = MutableStateFlow(false)

  val isSubmitTransactionLoading =
    combine(
      submitTransactionResourceConsumer.isLoading,
      submitTransactionResourceConsumer.data,
      isShowingBiometricPrompt,
    ) { isLoading, transactionSignature, isShowingBiometricPrompt ->
      isLoading || isShowingBiometricPrompt || transactionSignature != null
    }.distinctUntilChanged()
  val continueWithTransactionSignature =
    submitTransactionResourceConsumer.data.filterNotNull().asEventFlow(viewModelScope)

  fun onCreate() {
    getFeeResourceConsumer.collectFlow(
      solanaApiRepository.getRecentBlockhash().mapData { it.fee }
    )

    onEach(submitTransactionResourceConsumer.error.filterNotNull()) {
      _showError.tryEmit(errorMessageFactory.create(it))
    }
  }

  fun onSendClicked() {
    if (biometricManager.isAvailableOnDevice()) {
      isShowingBiometricPrompt.value = true
      _showBiometricAuthenticationPrompt.tryEmit(
        biometricManager.createPrompt(
          title = getString(R.string.send_unlock_biometrics_title),
          description = getString(R.string.send_unlock_biometrics_description)
        )
      )
    } else {
      upgradeSessionAndSend()
    }
  }

  fun onBiometricPromptResult(result: BiometricPromptResult) {
    isShowingBiometricPrompt.value = false
    if (result == BiometricPromptResult.SUCCESS) {
      upgradeSessionAndSend()
    } else if (result == BiometricPromptResult.FAIL) {
      _showError.tryEmit(getString(R.string.send_error_invalid_biometrics))
    }
  }

  private fun upgradeSessionAndSend() {
    sessionManager.upgradeSession()
    val session = sessionManager.activeSession.value
    if (session is KeyPairSession) {
      send(session.keyPair)
    } else {
      _showError.tryEmit(
        getString(R.string.send_error_accessing_private_key)
      )
    }
  }

  private fun send(keyPair: KeyPair) {
    submitTransactionResourceConsumer.collectFlow(
      solanaApiRepository.send(
        keyPair.privateKey,
        transferRequest.recipient,
        checkNotNull(transferRequest.amount).solToLamports(),
        transferRequest.memo,
      )
    )
  }
}