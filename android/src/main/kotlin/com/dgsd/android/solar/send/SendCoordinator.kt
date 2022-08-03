package com.dgsd.android.solar.send

import androidx.lifecycle.ViewModel
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.ksol.model.PublicKey
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
    PREVIOUS_ADDRESS_PICKER,
  }

  sealed interface Destination {
    object ScanQR : Destination
    object PreviousTransactionPicker : Destination
    object TransferRequestConfirmation : Destination
    object TransactionRequestConfirmation : Destination
    object EnterAddress : Destination
    object EnterAmount : Destination
    object Confirmation : Destination
    object Success : Destination
  }

  private val _destination = MutableEventFlow<Destination>()
  val destination = _destination.asEventFlow()

  var solPayRequest: SolPayRequest? = solPayRequestUrl?.let { solPay.parseUrl(it) }
    private set

  var inputtedAddress: PublicKey? = null
    private set

  fun onCreate() {
    if (startingDestination != null) {
      _destination.tryEmit(
        when (startingDestination) {
          StartingDestination.QR_SCAN -> Destination.ScanQR
          StartingDestination.ENTER_ADDRESS -> Destination.EnterAddress
          StartingDestination.PREVIOUS_ADDRESS_PICKER -> Destination.PreviousTransactionPicker
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
    when (solPayRequest) {
      is SolPayTransactionRequest -> _destination.tryEmit(Destination.TransferRequestConfirmation)
      is SolPayTransferRequest -> _destination.tryEmit(Destination.TransferRequestConfirmation)
    }
  }

  fun navigateToEnterAmount() {
    _destination.tryEmit(Destination.EnterAmount)
  }

  fun navigateWithAddressInput(address: PublicKey) {
    inputtedAddress = address
    _destination.tryEmit(Destination.EnterAmount)
  }
}