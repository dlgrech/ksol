package com.dgsd.android.solar.onboarding.createaccount

import androidx.lifecycle.ViewModel
import com.dgsd.android.solar.common.model.SensitiveList
import com.dgsd.android.solar.common.model.SensitiveString

class CreateAccountAddressSelectionViewModel(
    private val passphrase: SensitiveString,
    private val seedPhrase: SensitiveList<String>,
) : ViewModel() {
}