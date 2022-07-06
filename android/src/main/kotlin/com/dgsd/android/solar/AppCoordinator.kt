package com.dgsd.android.solar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.android.solar.session.model.KeyPairSession
import com.dgsd.android.solar.session.model.NoActiveWalletSession
import com.dgsd.android.solar.session.model.PublicKeySession
import com.dgsd.android.solar.session.model.Session
import kotlinx.coroutines.flow.*

class AppCoordinator(
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _destination = MutableStateFlow<Destination?>(null)
    val destination = _destination.filterNotNull().distinctUntilChanged()

    sealed interface Destination {
        object Onboarding : Destination
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
                _destination.value = Destination.Onboarding
            }

            is KeyPairSession,
            is PublicKeySession -> Destination.Home
        }
    }
}