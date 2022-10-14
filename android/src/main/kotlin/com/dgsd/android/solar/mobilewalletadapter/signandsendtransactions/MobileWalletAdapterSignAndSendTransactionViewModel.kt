package com.dgsd.android.solar.mobilewalletadapter.signandsendtransactions

import android.app.Application
import android.net.Uri
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.applock.biometrics.AppLockBiometricManager
import com.dgsd.android.solar.applock.biometrics.BiometricPromptResult
import com.dgsd.android.solar.common.model.Resource
import com.dgsd.android.solar.common.ui.PublicKeyFormatter
import com.dgsd.android.solar.common.util.*
import com.dgsd.android.solar.extensions.getString
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.mobilewalletadapter.util.createTransactionSummaryString
import com.dgsd.android.solar.repository.SolanaApiRepository
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.android.solar.session.model.KeyPairSession
import com.dgsd.android.solar.session.model.WalletSession
import com.dgsd.ksol.core.LocalTransactions
import com.dgsd.ksol.core.model.KeyPair
import com.dgsd.ksol.core.model.LocalTransaction
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.model.TransactionSignature
import com.solana.mobilewalletadapter.walletlib.scenario.SignAndSendTransactionsRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class MobileWalletAdapterSignAndSendTransactionViewModel(
  application: Application,
  private val signAndSendTransactionsRequest: SignAndSendTransactionsRequest,
  private val sessionManager: SessionManager,
  private val publicKeyFormatter: PublicKeyFormatter,
  private val biometricManager: AppLockBiometricManager,
  private val solanaApiRepository: SolanaApiRepository,
) : AndroidViewModel(application) {

  private val deserializeTransactionsResourceConsumer =
    ResourceFlowConsumer<List<LocalTransaction>>(viewModelScope)

  private val signTransactionsResourceConsumer =
    ResourceFlowConsumer<List<TransactionSignature>>(viewModelScope)

  val requesterName = stateFlowOf {
    signAndSendTransactionsRequest.identityName
      ?: getString(R.string.mobile_wallet_adapter_unknown_requester)
  }

  val requesterIconUrl = stateFlowOf {
    val base = signAndSendTransactionsRequest.identityUri
    val iconPath = signAndSendTransactionsRequest.iconRelativeUri

    if (base == null || iconPath == null) {
      null
    } else {
      Uri.withAppendedPath(base, iconPath.encodedPath).toString()
    }
  }

  val requestUrl = stateFlowOf {
    signAndSendTransactionsRequest.identityUri?.toString()
  }

  val isAuthorizationButtonsVisible = deserializeTransactionsResourceConsumer.data.map {
    !it.isNullOrEmpty()
  }

  val transactionSummaries =
    deserializeTransactionsResourceConsumer.data.filterNotNull().map { transactions ->
      transactions.map {
        createTransactionSummaryString(
          context = application,
          session = sessionManager.activeSession.value as WalletSession,
          transaction = it,
          publicKeyFormatter = publicKeyFormatter,
        )
      }
    }

  private val isShowingBiometricPrompt = MutableStateFlow(false)

  private val _showBiometricAuthenticationPrompt = MutableEventFlow<BiometricPrompt.PromptInfo>()
  val showBiometricAuthenticationPrompt = _showBiometricAuthenticationPrompt.asEventFlow()

  val showSigningLoadingIndicator = signTransactionsResourceConsumer.isLoadingOrError

  fun onCreate() {
    deserializeTransactionsResourceConsumer.collectFlow(
      resourceFlowOf {
        signAndSendTransactionsRequest.payloads.map { LocalTransactions.deserializeTransaction(it) }
      }
    )

    onEach(deserializeTransactionsResourceConsumer.error.filterNotNull()) {
      signAndSendTransactionsRequest.completeWithDecline()
    }

    onEach(signTransactionsResourceConsumer.data.filterNotNull()) { signatures ->
      signAndSendTransactionsRequest.completeWithSignatures(
        signatures.map { it.toByteArray() }.toTypedArray()
      )
    }

    onEach(signTransactionsResourceConsumer.error.filterNotNull()) {
      signAndSendTransactionsRequest.completeWithDecline()
    }
  }

  fun onSignClicked() {
    if (biometricManager.isAvailableOnDevice()) {
      isShowingBiometricPrompt.value = true
      _showBiometricAuthenticationPrompt.tryEmit(
        biometricManager.createPrompt(
          title = getString(R.string.send_unlock_biometrics_title),
          description = getString(R.string.sign_unlock_biometrics_description)
        )
      )
    } else {
      upgradeSessionAndSendTransactions()
    }
  }

  fun onDeclineClicked() {
    signAndSendTransactionsRequest.completeWithDecline()
  }

  fun onBiometricPromptResult(result: BiometricPromptResult) {
    isShowingBiometricPrompt.value = false
    if (result == BiometricPromptResult.SUCCESS) {
      upgradeSessionAndSendTransactions()
    } else if (result == BiometricPromptResult.FAIL) {
      signAndSendTransactionsRequest.completeWithAuthorizationNotValid()
    }
  }

  private fun upgradeSessionAndSendTransactions() {
    sessionManager.upgradeSession()
    val session = sessionManager.activeSession.value
    if (session is KeyPairSession) {
      signTransactions(session.keyPair)
    } else {
      signAndSendTransactionsRequest.completeWithDecline()
    }
  }

  private fun signTransactions(keyPair: KeyPair) {
    val unsignedTransactions = deserializeTransactionsResourceConsumer.data.value.orEmpty()
    if (unsignedTransactions.isEmpty()) {
      signAndSendTransactionsRequest.completeWithDecline()
    } else {
      signTransactionsResourceConsumer.collectFlow(
        solanaApiRepository.getRecentBlockhash()
          .mapData { it.blockhash }
          .flatMapSuccess { blockHash ->
            combine(
              unsignedTransactions
                .map { transaction ->
                  transaction.copy(
                    message = transaction.message.copy(
                      recentBlockhash = PublicKey.fromBase58(blockHash)
                    )
                  )
                }
                .map { transaction ->
                  solanaApiRepository.signAndSend(keyPair.privateKey, transaction)
                }
            ) { sendResources ->
              val allSuccess = sendResources.all { it is Resource.Success }
              val error = sendResources.filterIsInstance<Resource.Error<*>>().firstOrNull()

              if (allSuccess) {
                Resource.Success(sendResources.map { (it as Resource.Success).data })
              } else if (error != null) {
                Resource.Error(error.error)
              } else {
                Resource.Loading()
              }
            }
          }
      )
    }
  }
}