package com.dgsd.android.solar.send

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.applock.biometrics.AppLockBiometricManager
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.common.ui.PublicKeyFormatter
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.common.util.resourceFlowOf
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.android.solar.session.model.WalletSession
import com.dgsd.ksol.model.LocalTransaction
import com.dgsd.ksol.solpay.SolPay
import com.dgsd.ksol.solpay.model.SolPayTransactionRequest

class SendConfirmTransactionRequestViewModel(
  application: Application,
  private val session: WalletSession,
  private val sessionManager: SessionManager,
  private val publicKeyFormatter: PublicKeyFormatter,
  private val errorMessageFactory: ErrorMessageFactory,
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

  fun onCreate() {
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
  }
}