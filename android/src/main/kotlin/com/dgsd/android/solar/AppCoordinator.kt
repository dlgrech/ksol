package com.dgsd.android.solar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.android.solar.session.model.KeyPairSession
import com.dgsd.android.solar.session.model.NoActiveWalletSession
import com.dgsd.android.solar.session.model.PublicKeySession
import com.dgsd.android.solar.session.model.Session
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AppCoordinator(
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _destination = MutableStateFlow<Destination?>(null)
    val destination = _destination.value

    sealed interface Destination {
        object NoActiveWallet : Destination
        object Home : Destination
    }

    init {
        sessionManager.activeSession
            .distinctUntilChangedBy { it.sessionId }
            .onEach { onSessionChanged(it) }
            .launchIn(viewModelScope)
    }

    private fun onSessionChanged(session: Session) {
        when (session) {
            NoActiveWalletSession -> {
                _destination.value = Destination.NoActiveWallet
            }

            is KeyPairSession,
            is PublicKeySession -> Destination.Home
        }
    }
}