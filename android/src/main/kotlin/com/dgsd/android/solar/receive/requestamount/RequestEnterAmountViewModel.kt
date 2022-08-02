package com.dgsd.android.solar.receive.requestamount

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.extensions.getString
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.ksol.model.LAMPORTS_IN_SOL
import com.dgsd.ksol.model.Lamports
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal

class RequestEnterAmountViewModel(
  application: Application,
  private val errorMessageFactory: ErrorMessageFactory,
) : AndroidViewModel(application) {

  private val rawInput = MutableStateFlow("")
  val isNextButtonEnabled = rawInput.map { it.isNotEmpty() }

  private val _errorMessage = MutableStateFlow<CharSequence?>(null)
  val errorMessage = _errorMessage.asStateFlow()

  private val _continueWithLamports = MutableEventFlow<Lamports>()
  val continueWithLamports = _continueWithLamports.asEventFlow()

  fun onInputChanged(text: String) {
    rawInput.value = text
    _errorMessage.value = null
  }

  fun onNextButtonClicked() {
    val input = rawInput.value
    runCatching {
      BigDecimal(input)
    }.onFailure { error ->
      _errorMessage.value = errorMessageFactory.create(
        error,
        getString(R.string.receive_request_amount_amount_input_error_invalid_amount)
      )
    }.onSuccess { bigDecimalAmount ->
      if (LAMPORTS_IN_SOL * bigDecimalAmount < BigDecimal.ONE) {
        // Invalid number of decimals
        _errorMessage.value =
          getString(R.string.receive_request_amount_amount_input_error_invalid_amount)
      } else {
        _continueWithLamports.tryEmit((bigDecimalAmount * LAMPORTS_IN_SOL).longValueExact())
      }
    }
  }
}