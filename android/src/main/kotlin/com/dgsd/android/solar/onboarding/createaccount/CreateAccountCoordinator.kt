package com.dgsd.android.solar.onboarding.createaccount

import androidx.lifecycle.ViewModel
import com.dgsd.android.solar.common.model.SensitiveList
import com.dgsd.android.solar.common.model.SensitiveString
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.ksol.model.KeyPair

class CreateAccountCoordinator: ViewModel() {

    sealed interface Destination {
        object EnterPassphrase : Destination
        object ViewSeedPhrase : Destination
        object Confirmation : Destination
    }

    private val _destination = MutableEventFlow<Destination>()
    val destination = _destination.asEventFlow()

    var passphrase: SensitiveString? = null
        private set

    var seedPhrase: SensitiveList<String>? = null
        private set

    fun onCreate() {
        _destination.tryEmit(Destination.EnterPassphrase)
    }

    fun onPassphraseConfirmed(passphrase: SensitiveString?) {
        this.passphrase = passphrase
        _destination.tryEmit(Destination.ViewSeedPhrase)
    }

    fun onSeedPhraseConfirmed(seedPhrase: SensitiveList<String>) {
        this.seedPhrase = seedPhrase
        _destination.tryEmit(Destination.Confirmation)
    }
}