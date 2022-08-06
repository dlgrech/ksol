package com.dgsd.android.solar.send

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.applock.biometrics.AppLockBiometricManager
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.common.ui.PublicKeyFormatter
import com.dgsd.android.solar.common.ui.SolTokenFormatter
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.common.util.mapData
import com.dgsd.android.solar.common.util.resourceFlowOf
import com.dgsd.android.solar.extensions.getString
import com.dgsd.android.solar.extensions.getSystemProgramInstruction
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.repository.SolanaApiRepository
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.android.solar.session.model.WalletSession
import com.dgsd.ksol.model.Lamports
import com.dgsd.ksol.model.LocalTransaction
import com.dgsd.ksol.model.TransactionSignature
import com.dgsd.ksol.solpay.SolPay
import com.dgsd.ksol.solpay.model.SolPayTransactionRequest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class SendConfirmTransactionRequestViewModel(
  application: Application,
  private val session: WalletSession,
  private val sessionManager: SessionManager,
  private val publicKeyFormatter: PublicKeyFormatter,
  private val errorMessageFactory: ErrorMessageFactory,
  private val solanaApiRepository: SolanaApiRepository,
  private val solPay: SolPay,
  private val transactionRequest: SolPayTransactionRequest,
  private val biometricManager: AppLockBiometricManager,
) : AndroidViewModel(application) {

  private data class TransactionRequestInfo(
    val label: String?,
    val iconUrl: String?,
    val message: String?,
    val transaction: LocalTransaction,
  )

  private val transactionDetailsResourceConsumer =
    ResourceFlowConsumer<TransactionRequestInfo>(viewModelScope)

  private val submitTransactionResourceConsumer =
    ResourceFlowConsumer<TransactionSignature>(viewModelScope)

  private val getFeeResourceConsumer = ResourceFlowConsumer<Lamports>(viewModelScope)

  val logoUrl = transactionDetailsResourceConsumer.data.map { it?.iconUrl }
  val label = transactionDetailsResourceConsumer.data.map { it?.label }
  val message = transactionDetailsResourceConsumer.data.map { it?.message }
  val recipientText = transactionDetailsResourceConsumer.data.map { requestInfo ->
    requestInfo
      ?.transaction
      ?.message
      ?.accountKeys
      ?.firstOrNull {  account -> account.isWritable && account.publicKey != session.publicKey }
      ?.publicKey
      ?.let(publicKeyFormatter::format)
  }
  val feeText = combine(
    transactionDetailsResourceConsumer.data,
    getFeeResourceConsumer.data,
    getFeeResourceConsumer.error
  ) { requestInfo, fee, error ->
    if (requestInfo == null) {
      null
    } else {
      val feePayer = requestInfo.transaction.message.accountKeys.firstOrNull { it.isFeePayer }

      if (fee != null && feePayer?.publicKey == session.publicKey) {
        SolTokenFormatter.format(fee)
      } else if (error != null) {
        getString(R.string.send_transfer_request_confirmation_error_getting_fee)
      } else {
        null
      }
    }
  }

  val amountText = transactionDetailsResourceConsumer.data.map {
    val systemProgramInstruction = it?.transaction?.message?.getSystemProgramInstruction()
    if (systemProgramInstruction == null) {
      null
    } else {
      SolTokenFormatter.format(systemProgramInstruction.lamports)
    }
  }

  val isLoading = transactionDetailsResourceConsumer.isLoadingOrError
  val isLoadingFee = getFeeResourceConsumer.isLoading

  private val _showError = MutableEventFlow<CharSequence>()
  val showError = _showError.asEventFlow()

  val continueWithTransactionSignature =
    submitTransactionResourceConsumer.data.filterNotNull().asEventFlow(viewModelScope)

  fun onCreate() {
    getFeeResourceConsumer.collectFlow(
      solanaApiRepository.getRecentBlockhash().mapData { it.fee }
    )

    transactionDetailsResourceConsumer.collectFlow(
      resourceFlowOf {
        val transactionInfo = solPay.getTransaction(session.publicKey, transactionRequest)
        val requestDetails = solPay.getDetails(transactionRequest)

        TransactionRequestInfo(
          label = requestDetails.label,
          iconUrl = requestDetails.iconUrl,
          message = transactionInfo.message,
          transaction = transactionInfo.transaction
        )
      }
    )

    onEach(submitTransactionResourceConsumer.error.filterNotNull()) {
      _showError.tryEmit(errorMessageFactory.create(it))
    }
  }

  fun onSendClicked() {

  }
}