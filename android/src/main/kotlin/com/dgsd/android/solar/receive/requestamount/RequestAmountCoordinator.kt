package com.dgsd.android.solar.receive.requestamount

import androidx.lifecycle.ViewModel
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.ksol.model.Lamports

class RequestAmountCoordinator : ViewModel() {

  sealed interface Destination {
    object EnterAmount : Destination
    object ViewQR : Destination
  }

  private val _destination = MutableEventFlow<Destination>()
  val destination = _destination.asEventFlow()

  var lamports: Lamports? = null
    private set

  fun onCreate() {
    _destination.tryEmit(Destination.EnterAmount)
  }

  fun onAmountEntered(lamports: Lamports) {
    this.lamports = lamports
    _destination.tryEmit(Destination.ViewQR)
  }
}