package com.dgsd.android.solar.send

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.common.ui.PublicKeyFormatter
import com.dgsd.android.solar.common.ui.SolTokenFormatter
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.common.util.stateFlowOf
import com.dgsd.android.solar.repository.SolanaApiRepository
import com.dgsd.ksol.model.TransactionSignature
import com.dgsd.ksol.solpay.model.SolPayTransferRequest
import com.dgsd.ksol.utils.solToLamports

class SendConfirmTransferRequestViewModel(
  private val application: Application,
  private val transferRequest: SolPayTransferRequest,
  private val publicKeyFormatter: PublicKeyFormatter,
  private val errorMessageFactory: ErrorMessageFactory,
  private val solanaApiRepository: SolanaApiRepository,
) : ViewModel() {

  private val submitTransactionResourceConsumer =
    ResourceFlowConsumer<TransactionSignature>(viewModelScope)

  val recipientText = stateFlowOf {
    publicKeyFormatter.format(transferRequest.recipient)
  }

  val amountText = stateFlowOf {
    SolTokenFormatter.formatLong(checkNotNull(transferRequest.amount?.solToLamports()))
  }

  val messageText = stateFlowOf { transferRequest.message }
  val labelText = stateFlowOf { transferRequest.label }
  val memoText = stateFlowOf { transferRequest.memo }

  val feeText = stateFlowOf {
    // TODO: Call getFeesForMessages JSON RPC
    ""
  }

  fun onSendClicked() {

  }
}