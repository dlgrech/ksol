package com.dgsd.android.solar.onboarding.createaccount

import androidx.lifecycle.ViewModel
import com.dgsd.android.solar.common.model.SensitiveString
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.SimpleMutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.flow.call

class CreateAccountEnterPassphraseViewModel : ViewModel() {

  private val _continueWithPassphrase = MutableEventFlow<SensitiveString>()
  val continueWithPassphrase = _continueWithPassphrase.asEventFlow()

  private val _showContinueWithoutPassphraseWarning = SimpleMutableEventFlow()
  val showContinueWithoutPassphraseWarning = _showContinueWithoutPassphraseWarning.asEventFlow()

  fun onContinueClicked(passphrase: String?) {
    if (passphrase.isNullOrEmpty()) {
      _showContinueWithoutPassphraseWarning.call()
    } else {
      _continueWithPassphrase.tryEmit(SensitiveString(passphrase))
    }
  }

  fun onSkipClicked() {
    _showContinueWithoutPassphraseWarning.call()
  }

  fun onSkipConfirmed() {
    _continueWithPassphrase.tryEmit(SensitiveString(""))
  }
}