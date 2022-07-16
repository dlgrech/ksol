package com.dgsd.android.solar.onboarding.restoreaccount

import androidx.lifecycle.ViewModel
import com.dgsd.android.solar.common.model.SensitiveList
import com.dgsd.android.solar.common.model.SensitiveString
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.ksol.model.KeyPair

class RestoreAccountCoordinator(
    private val sessionManager: SessionManager,
) : ViewModel() {

    sealed interface Destination {
        object EnterSeedPhrase : Destination
        object SelectAccount : Destination
    }

    private val _destination = MutableEventFlow<Destination>()
    val destination = _destination.asEventFlow()

    var seedKeyPair: KeyPair? = null
        private set

    var seedPhrase: SensitiveList<String>? = null
        private set

    fun onCreate() {
        _destination.tryEmit(Destination.EnterSeedPhrase)
    }

    fun onSeedGenerated(seedPhrase: SensitiveList<String>, keyPair: KeyPair) {
        this.seedKeyPair = keyPair
        this.seedPhrase = seedPhrase

        _destination.tryEmit(Destination.SelectAccount)
    }
}