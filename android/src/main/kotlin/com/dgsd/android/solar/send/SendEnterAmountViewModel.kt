package com.dgsd.android.solar.send

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.common.ui.PublicKeyFormatter
import com.dgsd.android.solar.common.ui.RichTextFormatter
import com.dgsd.android.solar.common.ui.SolTokenFormatter
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.common.util.mapData
import com.dgsd.android.solar.extensions.getString
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.repository.SolanaApiRepository
import com.dgsd.ksol.core.model.Lamports
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.utils.isValidSolAmount
import com.dgsd.ksol.core.utils.solToLamports
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import java.math.BigDecimal

class SendEnterAmountViewModel(
  application: Application,
  private val errorMessageFactory: ErrorMessageFactory,
  private val solanaApiRepository: SolanaApiRepository,
  publicKeyFormatter: PublicKeyFormatter,
  sendingToAddress: PublicKey?,
) : AndroidViewModel(application) {

  private val balanceResourceConsumer = ResourceFlowConsumer<Lamports>(viewModelScope)

  val pageInstructionsText = MutableStateFlow(
    if (sendingToAddress == null) {
      getString(R.string.send_enter_amount_page_explainer)
    } else {
      RichTextFormatter.expandTemplate(
        application,
        R.string.send_enter_amount_page_explainer_with_address_template,
        RichTextFormatter.bold(publicKeyFormatter.abbreviate(sendingToAddress))
      )
    }
  ).asStateFlow()

  private val _errorMessage = MutableEventFlow<CharSequence>()
  val errorMessage = _errorMessage.asEventFlow()

  private val rawInput = MutableStateFlow("")
  val isNextButtonEnabled = rawInput.map { it.isNotEmpty() }

  private val _continueWithLamports = MutableEventFlow<Lamports>()
  val continueWithLamports = _continueWithLamports.asEventFlow()

  val balanceText = balanceResourceConsumer.data.filterNotNull().map {
    RichTextFormatter.expandTemplate(
      application,
      R.string.send_enter_amount_balance_template,
      RichTextFormatter.bold(SolTokenFormatter.format(it))
    )
  }

  fun onCreate() {
    balanceResourceConsumer.collectFlow(
      solanaApiRepository.getBalance().mapData { it.lamports }
    )
  }

  fun onInputChanged(text: String) {
    rawInput.value = text
  }

  fun onNextButtonClicked() {
    val input = rawInput.value
    runCatching {
      BigDecimal(input)
    }.onFailure { error ->
      _errorMessage.tryEmit(
        errorMessageFactory.create(
          error,
          getString(R.string.send_enter_amount_amount_input_error_invalid_amount)
        )
      )
    }.onSuccess { bigDecimalAmount ->
      if (!bigDecimalAmount.isValidSolAmount()) {
        // Invalid number of decimals
        _errorMessage.tryEmit(
          getString(R.string.send_enter_amount_amount_input_error_invalid_amount)
        )
      } else {
        val lamports = bigDecimalAmount.solToLamports()
        if (lamports == 0L) {
          _errorMessage.tryEmit(
            getString(R.string.send_enter_amount_amount_input_error_invalid_amount)
          )
        } else {
          val balance = balanceResourceConsumer.data.value
          if (balance == null) {
            // We don't know the balance - just let the user through.
            // If there's a problem, the transaction will fail
            _continueWithLamports.tryEmit(lamports)
          } else if (lamports <= balance) {
            _continueWithLamports.tryEmit(lamports)
          } else {
            _errorMessage.tryEmit(
              getString(R.string.send_enter_amount_not_enough_balance)
            )
          }
        }
      }
    }
  }
}