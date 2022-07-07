package com.dgsd.android.solar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.android.solar.session.model.KeyPairSession
import com.dgsd.android.solar.session.model.NoActiveWalletSession
import com.dgsd.android.solar.session.model.PublicKeySession
import com.dgsd.android.solar.session.model.Session
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AppCoordinator(
    sessionManager: SessionManager,
) : ViewModel() {

    sealed interface Destination {
        object Onboarding : Destination
        object Home : Destination
    }

    private val _destination = MutableEventFlow<Destination>()
    val destination = _destination.asEventFlow()

    init {
        sessionManager.activeSession
            .distinctUntilChangedBy { it.sessionId }
            .onEach { onSessionChanged(it) }
            .launchIn(viewModelScope)
    }

    private fun onSessionChanged(session: Session) {
        when (session) {
            NoActiveWalletSession -> {
                _destination.tryEmit(Destination.Onboarding)
            }

            is KeyPairSession,
            is PublicKeySession -> Destination.Home
        }
    }
}