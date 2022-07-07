package com.dgsd.android.solar.onboarding.createaccount

import androidx.lifecycle.ViewModel
import com.dgsd.android.solar.common.model.SensitiveList
import com.dgsd.android.solar.common.model.SensitiveString
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow

class CreateAccountCoordinator : ViewModel() {

    sealed interface Destination {
        object EnterPassphrase : Destination
        object ViewSeedPhrase : Destination
        object AddressSelection : Destination
    }

    private val _destination = MutableEventFlow<Destination>()
    val destination = _destination.asEventFlow()

    private var passphrase: SensitiveString? = null
    private var seedPhrase: SensitiveList<String>? = null

    fun onCreate() {
        _destination.tryEmit(Destination.EnterPassphrase)
    }

    fun onPassphraseConfirmed(passphrase: SensitiveString) {
        this.passphrase = passphrase
        _destination.tryEmit(Destination.ViewSeedPhrase)
    }

    fun onSeedPhraseConfirmed(seedPhrase: SensitiveList<String>) {
        this.seedPhrase = seedPhrase
        _destination.tryEmit(Destination.AddressSelection)
    }
}