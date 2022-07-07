package com.dgsd.android.solar.onboarding.createaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.common.model.Resource
import com.dgsd.android.solar.common.model.SensitiveString
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.common.util.anyTrue
import com.dgsd.android.solar.common.util.execute
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.ksol.keygen.KeyFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CreateAccountEnterPassphraseViewModel() : ViewModel() {

    private val _continueWithPassphrase = MutableEventFlow<SensitiveString>()
    val continueWithPassphrase = _continueWithPassphrase.asEventFlow()

    fun onContinueClicked() {
        _continueWithPassphrase.tryEmit(SensitiveString("Temporary passphrase"))
    }
}