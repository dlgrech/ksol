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
  }

  sealed interface Destination {
    object ScanQR : Destination
    object PreviousTransactionPicker : Destination
    object DetectedRecipient : Destination
    object EnterAddress : Destination
    object EnterAmount : Destination
    object SendToPrevious : Destination
    object Confirmation : Destination
    object Success : Destination
  }

  private val _destination = MutableEventFlow<Destination>()
  val destination = _destination.asEventFlow()

  fun onCreate() {
    _destination.tryEmit(
      when (startingDestination) {
        StartingDestination.QR_SCAN -> Destination.ScanQR
        StartingDestination.ENTER_ADDRESS -> Destination.EnterAddress
      }
    )
  }

  fun navigateWithTransactionRequest(request: SolPayTransactionRequest) {

  }

  fun navigateWithTransferRequest(request: SolPayTransferRequest) {

  }
}