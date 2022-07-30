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
import com.dgsd.ksol.model.TransactionSignature
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AppCoordinator(
    private val sessionManager: SessionManager,
) : ViewModel() {

    sealed interface Destination {
        object Onboarding : Destination
        object Home : Destination
        object Settings : Destination
        object ShareWalletAddress : Destination
        object TransactionList : Destination
        object RequestAmount : Destination

        data class TransactionDetails(val signature: TransactionSignature): Destination
    }

    private val _destination = MutableEventFlow<Destination>()
    val destination = _destination.asEventFlow()

    fun onCreate() {
        sessionManager.activeSession
            .distinctUntilChangedBy { it.sessionId }
            .onEach { onSessionChanged(it) }
            .launchIn(viewModelScope)
    }

    fun navigateToSettings() {
        _destination.tryEmit(Destination.Settings)
    }

    fun navigateToTransactionDetails(signature: TransactionSignature) {
        _destination.tryEmit(Destination.TransactionDetails(signature))
    }

    fun navigateToShareWalletAddress() {
        _destination.tryEmit(Destination.ShareWalletAddress)
    }

    fun navigateToRequestAmount() {
        _destination.tryEmit(Destination.RequestAmount)
    }

    fun navigateToTransactionList() {
        _destination.tryEmit(Destination.TransactionList)
    }

    private fun onSessionChanged(session: Session) {
        when (session) {
            NoActiveWalletSession -> {
                _destination.tryEmit(Destination.Onboarding)
            }

            is KeyPairSession,
            is PublicKeySession -> {
                _destination.tryEmit(Destination.Home)
            }
        }
    }
}