package com.dgsd.android.solar.send

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.common.ui.PublicKeyFormatter
import com.dgsd.android.solar.common.ui.SolTokenFormatter
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.common.util.mapData
import com.dgsd.android.solar.common.util.stateFlowOf
import com.dgsd.android.solar.extensions.getString
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.repository.SolanaApiRepository
import com.dgsd.ksol.model.Lamports
import com.dgsd.ksol.model.TransactionSignature
import com.dgsd.ksol.solpay.model.SolPayTransferRequest
import com.dgsd.ksol.utils.solToLamports
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine

class SendConfirmTransferRequestViewModel(
  application: Application,
  private val transferRequest: SolPayTransferRequest,
  private val publicKeyFormatter: PublicKeyFormatter,
  private val errorMessageFactory: ErrorMessageFactory,
  private val solanaApiRepository: SolanaApiRepository,
) : AndroidViewModel(application) {

  private val submitTransactionResourceConsumer =
    ResourceFlowConsumer<TransactionSignature>(viewModelScope)

  private val getFeeResourceConsumer = ResourceFlowConsumer<Lamports>(viewModelScope)

  val recipientText = stateFlowOf {
    publicKeyFormatter.format(transferRequest.recipient)
  }

  val amountText = stateFlowOf {
    SolTokenFormatter.formatLong(checkNotNull(transferRequest.amount?.solToLamports()))
  }

  val messageText = stateFlowOf { transferRequest.message }
  val labelText = stateFlowOf { transferRequest.label }
  val memoText = stateFlowOf { transferRequest.memo }

  val isLoadingFee = getFeeResourceConsumer.isLoading

  val feeText = combine(getFeeResourceConsumer.data, getFeeResourceConsumer.error) { fee, error ->
    if (fee != null) {
      SolTokenFormatter.formatLong(fee)
    } else if (error != null) {
      getString(R.string.send_transfer_request_confirmation_error_getting_fee)
    } else {
      ""
    }
  }

  fun onCreate() {
    getFeeResourceConsumer.collectFlow(
      solanaApiRepository.getRecentBlockhash().mapData { delay(2000); it.fee }
    )

    onEach(getFeeResourceConsumer.data) {
      println("HERE: data = $it")
    }

    onEach(getFeeResourceConsumer.error) {
      println("HERE: error = $it")
    }

    onEach(getFeeResourceConsumer.isLoading) {
      println("HERE: isLoading = $it")
    }
  }

  fun onSendClicked() {

  }
}