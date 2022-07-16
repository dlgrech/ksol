package com.dgsd.android.solar.onboarding

import androidx.lifecycle.ViewModel
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow

class OnboardingCoordinator : ViewModel() {

    sealed interface Destination {
        object Welcome : Destination
        object RestoreSeedPhraseFlow : Destination
        object CreateNewWalletFlow : Destination
    }

    private val _destination = MutableEventFlow<Destination>()
    val destination = _destination.asEventFlow()

    fun onCreate() {
        _destination.tryEmit(Destination.Welcome)
    }

    fun navigateToAddFromSeedPhrase() {
        _destination.tryEmit(Destination.RestoreSeedPhraseFlow)
    }

    fun navigateToCreateNewAccount() {
        _destination.tryEmit(Destination.CreateNewWalletFlow)
    }
}