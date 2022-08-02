package com.dgsd.android.solar.receive.requestamount

import androidx.lifecycle.ViewModel
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import kotlinx.coroutines.flow.MutableStateFlow

class RequestEnterMessageViewModel : ViewModel() {

  private val rawInput = MutableStateFlow("")

  private val _continueWithMessage = MutableEventFlow<String?>()
  val continueWithMessage = _continueWithMessage.asEventFlow()

  fun onInputChanged(text: String) {
    rawInput.value = text
  }

  fun onNextButtonClicked() {
    _continueWithMessage.tryEmit(rawInput.value)
  }
}