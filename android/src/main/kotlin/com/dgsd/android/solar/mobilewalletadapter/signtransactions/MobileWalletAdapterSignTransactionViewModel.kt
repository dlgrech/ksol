package com.dgsd.android.solar.mobilewalletadapter.signtransactions

import android.app.Application
import android.net.Uri
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.applock.biometrics.AppLockBiometricManager
import com.dgsd.android.solar.applock.biometrics.BiometricPromptResult
import com.dgsd.android.solar.common.ui.PublicKeyFormatter
import com.dgsd.android.solar.common.ui.RichTextFormatter
import com.dgsd.android.solar.common.ui.SolTokenFormatter
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.common.util.resourceFlowOf
import com.dgsd.android.solar.common.util.stateFlowOf
import com.dgsd.android.solar.extensions.*
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.android.solar.session.model.KeyPairSession
import com.dgsd.android.solar.session.model.WalletSession
import com.dgsd.ksol.LocalTransactions
import com.dgsd.ksol.model.KeyPair
import com.dgsd.ksol.model.LocalTransaction
import com.dgsd.ksol.programs.system.SystemProgramInstruction
import com.solana.mobilewalletadapter.walletlib.scenario.SignTransactionsRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class MobileWalletAdapterSignTransactionViewModel(
  application: Application,
  private val signTransactionsRequest: SignTransactionsRequest,
  private val sessionManager: SessionManager,
  private val publicKeyFormatter: PublicKeyFormatter,
  private val biometricManager: AppLockBiometricManager,
) : AndroidViewModel(application) {

  private val deserializeTransactionsResourceConsumer =
    ResourceFlowConsumer<List<LocalTransaction>>(viewModelScope)

  private val signTransactionsResourceConsumer =
    ResourceFlowConsumer<Array<ByteArray>>(viewModelScope)

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

  val transactionSummaries =
    deserializeTransactionsResourceConsumer.data.filterNotNull().map { transactions ->
      transactions.map { createTransactionSummaryString(it) }
    }

  private val isShowingBiometricPrompt = MutableStateFlow(false)

  private val _showBiometricAuthenticationPrompt = MutableEventFlow<BiometricPrompt.PromptInfo>()
  val showBiometricAuthenticationPrompt = _showBiometricAuthenticationPrompt.asEventFlow()

  val showSigningLoadingIndicator = signTransactionsResourceConsumer.isLoadingOrError

  fun onCreate() {
    deserializeTransactionsResourceConsumer.collectFlow(
      resourceFlowOf {
        signTransactionsRequest.payloads.map { LocalTransactions.deserializeTransaction(it) }
      }
    )

    onEach(deserializeTransactionsResourceConsumer.error.filterNotNull()) {
      signTransactionsRequest.completeWithDecline()
    }

    onEach(signTransactionsResourceConsumer.data.filterNotNull()) {
      signTransactionsRequest.completeWithSignedPayloads(it)
    }

    onEach(signTransactionsResourceConsumer.error.filterNotNull()) {
      signTransactionsRequest.completeWithDecline()
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
      upgradeSessionAndSignTransactions()
    }
  }

  fun onDeclineClicked() {
    signTransactionsRequest.completeWithDecline()
  }

  fun onBiometricPromptResult(result: BiometricPromptResult) {
    isShowingBiometricPrompt.value = false
    if (result == BiometricPromptResult.SUCCESS) {
      upgradeSessionAndSignTransactions()
    } else if (result == BiometricPromptResult.FAIL) {
      signTransactionsRequest.completeWithAuthorizationNotValid()
    }
  }

  private fun upgradeSessionAndSignTransactions() {
    sessionManager.upgradeSession()
    val session = sessionManager.activeSession.value
    if (session is KeyPairSession) {
      signTransactions(session.keyPair)
    } else {
      signTransactionsRequest.completeWithDecline()
    }
  }

  private fun signTransactions(keyPair: KeyPair) {
    val unsignedTransactions = deserializeTransactionsResourceConsumer.data.value.orEmpty()
    if (unsignedTransactions.isEmpty()) {
      signTransactionsRequest.completeWithDecline()
    } else {
      signTransactionsResourceConsumer.collectFlow(
        resourceFlowOf {
          unsignedTransactions.map {
            LocalTransactions.sign(it, keyPair)
          }.map {
            LocalTransactions.serialize(it)
          }.toTypedArray()
        }
      )
    }
  }

  private fun createTransactionSummaryString(transaction: LocalTransaction): CharSequence {
    val activeWallet = (sessionManager.activeSession.value as WalletSession).publicKey
    val recipient = transaction.message.extractBestDisplayRecipient(activeWallet)

    val systemProgramInfo = transaction.message.getSystemProgramInstruction()
    if (systemProgramInfo != null) {
      if (systemProgramInfo.instruction == SystemProgramInstruction.TRANSFER ||
        systemProgramInfo.instruction == SystemProgramInstruction.TRANSFER_WITH_SEED
      ) {
        return RichTextFormatter.expandTemplate(
          getApplication(),
          R.string.mobile_wallet_adapter_sign_transaction_summary_transfer_template,
          RichTextFormatter.bold(SolTokenFormatter.format(systemProgramInfo.lamports))
        )
      } else {
        return RichTextFormatter.expandTemplate(
          getApplication(),
          R.string.mobile_wallet_adapter_sign_transaction_summary_transaction_template,
          RichTextFormatter.bold(SolTokenFormatter.format(systemProgramInfo.lamports))
        )
      }
    }

    val memoMessage = transaction.message.getMemoMessage()
    if (memoMessage != null) {
      return RichTextFormatter.expandTemplate(
        getApplication(),
        R.string.mobile_wallet_adapter_sign_transaction_summary_memo_template,
        RichTextFormatter.bold(
          getString(R.string.mobile_wallet_adapter_sign_transaction_summary_create_memo)
        ),
        memoMessage
      )
    }

    if (recipient == null) {
      return getString(R.string.mobile_wallet_adapter_sign_transaction_unknown)
    } else {
      return RichTextFormatter.expandTemplate(
        getApplication(),
        R.string.mobile_wallet_adapter_sign_transaction_to_template,
        publicKeyFormatter.format(recipient)
      )
    }
  }
}