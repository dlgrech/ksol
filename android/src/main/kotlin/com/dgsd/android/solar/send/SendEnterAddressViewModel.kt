package com.dgsd.android.solar.send

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.clipboard.SystemClipboard
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.common.ui.PublicKeyFormatter
import com.dgsd.android.solar.common.ui.RichTextFormatter
import com.dgsd.android.solar.extensions.getString
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.session.model.WalletSession
import com.dgsd.ksol.model.PublicKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class SendEnterAddressViewModel(
  application: Application,
  private val session: WalletSession,
  private val systemClipboard: SystemClipboard,
  private val publicKeyFormatter: PublicKeyFormatter,
  private val errorMessageFactory: ErrorMessageFactory,
) : AndroidViewModel(application) {

  private val rawInput = MutableStateFlow("")

  private val addressOnClipboard = MutableStateFlow<PublicKey?>(null)

  val addressOnClipboardText = addressOnClipboard.map {
    if (it == null) {
      null
    } else {
      RichTextFormatter.expandTemplate(
        application,
        R.string.send_enter_address_send_to_clipboard_template,
        publicKeyFormatter.abbreviate(it)
      )
    }
  }

  private val _errorMessage = MutableEventFlow<CharSequence>()
  val errorMessage = _errorMessage.asEventFlow()

  private val _navigateWithAddress = MutableEventFlow<PublicKey>()
  val navigateWithAddress = _navigateWithAddress.asEventFlow()

  fun onCreate() {
    addressOnClipboard.value = systemClipboard.currentContents()?.let {
      runCatching { PublicKey.fromBase58(it) }.getOrNull()
    }
  }

  fun onTextChanged(text: String) {
    rawInput.value = text
  }

  fun onUseAddressOnClipboardClicked() {
    val address = addressOnClipboard.value
    if (address != null) {
      maybeNavigateToAddress(address)
    }
  }

  fun onNextButtonClicked() {
    val text = rawInput.value
    runCatching {
      PublicKey.fromBase58(text)
    }.onSuccess {
      maybeNavigateToAddress(it)
    }.onFailure {
      _errorMessage.tryEmit(
        errorMessageFactory.create(it, getString(R.string.send_enter_address_invalid_address))
      )
    }
  }

  private fun maybeNavigateToAddress(address: PublicKey) {
    if (address == session.publicKey) {
      _errorMessage.tryEmit(getString(R.string.send_enter_address_error_send_to_self))
    } else {
      _navigateWithAddress.tryEmit(address)
    }
  }
}