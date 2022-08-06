package com.dgsd.android.solar.send

import androidx.lifecycle.ViewModel
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.SimpleMutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.flow.call
import com.dgsd.ksol.model.Lamports
import com.dgsd.ksol.model.PublicKey
import com.dgsd.ksol.model.TransactionSignature
import com.dgsd.ksol.model.asSolAmount
import com.dgsd.ksol.solpay.SolPay
import com.dgsd.ksol.solpay.model.SolPayRequest
import com.dgsd.ksol.solpay.model.SolPayTransactionRequest
import com.dgsd.ksol.solpay.model.SolPayTransferRequest

class SendCoordinator(
  private val solPay: SolPay,
  private val startingDestination: StartingDestination?,
  solPayRequestUrl: String?,
) : ViewModel() {

  enum class StartingDestination {
    QR_SCAN,
    ENTER_ADDRESS,
  }

  sealed interface Destination {
    object ScanQR : Destination
    object TransferRequestConfirmation : Destination
    object TransactionRequestConfirmation : Destination
    object EnterAddress : Destination
    object EnterAmount : Destination
    object Confirmation : Destination
  }

  private val _destination = MutableEventFlow<Destination>()
  val destination = _destination.asEventFlow()

  var solPayRequest: SolPayRequest? = solPayRequestUrl?.let { solPay.parseUrl(it) }
    private set

  var inputtedAddress: PublicKey? = null
    private set

  var inputtedLamports: Lamports? = null
    private set

  var transactionSignature: TransactionSignature? = null
    private set

  private val _closeFlow = SimpleMutableEventFlow()
  val closeFlow = _closeFlow.asEventFlow()

  fun onCreate() {
    if (startingDestination != null) {
      _destination.tryEmit(
        when (startingDestination) {
          StartingDestination.QR_SCAN -> Destination.ScanQR
          StartingDestination.ENTER_ADDRESS -> Destination.EnterAddress
        }
      )
    } else if (solPayRequest != null) {
      navigateWithSolPayRequest(checkNotNull(solPayRequest))
    } else {
      _destination.tryEmit(Destination.EnterAddress)
    }
  }

  fun navigateWithSolPayRequest(request: SolPayRequest) {
    solPayRequest = request
    when (request) {
      is SolPayTransactionRequest -> _destination.tryEmit(Destination.TransactionRequestConfirmation)
      is SolPayTransferRequest -> {
        if (request.amount != null) {
          _destination.tryEmit(Destination.TransferRequestConfirmation)
        } else {
          _destination.tryEmit(Destination.EnterAmount)
        }
      }
    }
  }

  fun navigateToEnterAmount() {
    _destination.tryEmit(Destination.EnterAmount)
  }

  fun navigateWithAddressInput(address: PublicKey) {
    inputtedAddress = address
    navigateAfterAmountAndAddressEntry()
  }

  fun navigateWithAmountInput(lamports: Lamports) {
    inputtedLamports = lamports
    navigateAfterAmountAndAddressEntry()
  }

  fun navigateWithTransactionSignature(signature: TransactionSignature) {
    this.transactionSignature = signature
    _destination.tryEmit(Destination.Confirmation)
  }

  fun onCloseFlowClicked() {
    _closeFlow.call()
  }

  private fun navigateAfterAmountAndAddressEntry() {
    when {
      inputtedAddress != null && inputtedLamports != null -> {
        navigateWithSolPayRequest(
          SolPayTransferRequest(
            recipient = checkNotNull(inputtedAddress),
            amount = checkNotNull(inputtedLamports).asSolAmount()
          )
        )
      }

      solPayRequest is SolPayTransferRequest && inputtedLamports != null -> {
        navigateWithSolPayRequest(
          (solPayRequest as SolPayTransferRequest).copy(
            amount = checkNotNull(inputtedLamports).asSolAmount()
          )
        )
      }

      inputtedAddress != null -> {
        _destination.tryEmit(Destination.EnterAmount)
      }

      inputtedLamports != null -> {
        _destination.tryEmit(Destination.EnterAddress)
      }
    }
  }
}