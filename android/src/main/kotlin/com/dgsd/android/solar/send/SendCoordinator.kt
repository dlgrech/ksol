package com.dgsd.android.solar.send

import androidx.lifecycle.ViewModel
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow

class SendCoordinator : ViewModel() {

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
    _destination.tryEmit(Destination.EnterAddress)
  }
}