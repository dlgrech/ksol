package com.dgsd.android.solar.send

import androidx.lifecycle.ViewModel
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.ksol.solpay.model.SolPayTransactionRequest
import com.dgsd.ksol.solpay.model.SolPayTransferRequest

class SendCoordinator(
  private val startingDestination: StartingDestination,
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

  var transferRequest: SolPayTransferRequest? = null
    private set

  var transactionRequest: SolPayTransactionRequest? = null
    private set

  fun onCreate() {
    _destination.tryEmit(
      when (startingDestination) {
        StartingDestination.QR_SCAN -> Destination.ScanQR
        StartingDestination.ENTER_ADDRESS -> Destination.EnterAddress
        StartingDestination.PREVIOUS_ADDRESS_PICKER -> Destination.PreviousTransactionPicker
      }
    )
  }

  fun navigateWithTransactionRequest(request: SolPayTransactionRequest) {
    transactionRequest = request
    _destination.tryEmit(Destination.TransactionRequestConfirmation)
  }

  fun navigateWithTransferRequest(request: SolPayTransferRequest) {
    transferRequest = request
    _destination.tryEmit(Destination.TransferRequestConfirmation)
  }

  fun navigateToEnterAmount() {
    _destination.tryEmit(Destination.EnterAmount)
  }
}