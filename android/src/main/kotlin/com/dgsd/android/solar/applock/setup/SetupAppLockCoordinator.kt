package com.dgsd.android.solar.applock.setup

import androidx.lifecycle.ViewModel
import com.dgsd.android.solar.common.model.SensitiveString
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.SimpleMutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.flow.call

class SetupAppLockCoordinator : ViewModel() {

  sealed interface Destination {
    object EnterPin : Destination
    object ConfirmPin : Destination
  }

  private val _destination = MutableEventFlow<Destination>()
  val destination = _destination.asEventFlow()

  private val _continueWithPin = SimpleMutableEventFlow()
  val continueWithPin = _continueWithPin.asEventFlow()

  var enteredPin: SensitiveString? = null
    private set

  fun onCreate() {
    _destination.tryEmit(Destination.EnterPin)
  }

  fun navigateFromEnterPin(pin: SensitiveString) {
    enteredPin = pin
    _destination.tryEmit(Destination.ConfirmPin)
  }

  fun navigateFromConfirmPin() {
    _continueWithPin.call()
  }
}