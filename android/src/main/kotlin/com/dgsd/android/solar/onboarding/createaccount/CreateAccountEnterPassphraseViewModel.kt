package com.dgsd.android.solar.onboarding.createaccount

import androidx.lifecycle.ViewModel
import com.dgsd.android.solar.common.model.SensitiveString
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow

class CreateAccountEnterPassphraseViewModel : ViewModel() {

    private val _continueWithPassphrase = MutableEventFlow<SensitiveString>()
    val continueWithPassphrase = _continueWithPassphrase.asEventFlow()

    fun onContinueClicked() {
        _continueWithPassphrase.tryEmit(SensitiveString("Temporary passphrase"))
    }
}