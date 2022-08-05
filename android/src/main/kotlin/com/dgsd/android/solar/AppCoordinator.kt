package com.dgsd.android.solar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.applock.manager.AppLockManager
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.android.solar.session.model.*
import com.dgsd.ksol.model.TransactionSignature
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AppCoordinator(
  private val sessionManager: SessionManager,
  private val appLockManager: AppLockManager,
) : ViewModel() {

  sealed interface Destination {
    object Onboarding : Destination
    object AppEntryLock : Destination
    object Home : Destination
    object Settings : Destination
    object ShareWalletAddress : Destination
    object TransactionList : Destination
    object RequestAmount : Destination
    object SendWithQR : Destination
    object SendWithAddress : Destination
    object SendWithHistoricalAddress : Destination
    object SendWithNearby : Destination
    data class SendWithSolPayRequest(val requestUrl: String): Destination

    data class TransactionDetails(val signature: TransactionSignature) : Destination
  }

  private val _destination = MutableEventFlow<Destination>()
  val destination = _destination.asEventFlow()

  fun onCreate() {
    sessionManager.activeSession
      .distinctUntilChangedBy { it.sessionId }
      .onEach { onSessionChanged(it) }
      .launchIn(viewModelScope)
  }

  fun onResume() {
    if (appLockManager.shouldShowAppLockEntry()) {
      sessionManager.lockSession()
    }
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

  fun navigateToSendWithAddress() {
    _destination.tryEmit(Destination.SendWithAddress)
  }

  fun navigateToSendWithHistoricalAddress() {
    _destination.tryEmit(Destination.SendWithHistoricalAddress)
  }

  fun navigateToSendWithNearby() {
    _destination.tryEmit(Destination.SendWithNearby)
  }

  fun navigateToSendWithQrCode() {
    _destination.tryEmit(Destination.SendWithQR)
  }

  fun navigateToSendWithSolPayRequest(solPayRequestUrl: String) {
    _destination.tryEmit(Destination.SendWithSolPayRequest(solPayRequestUrl))
  }

  private fun onSessionChanged(session: Session) {
    when (session) {
      NoActiveWalletSession -> {
        _destination.tryEmit(Destination.Onboarding)
      }

      is LockedAppSession -> {
        _destination.tryEmit(Destination.AppEntryLock)
      }

      is WalletSession -> {
        _destination.tryEmit(Destination.Home)
      }
    }
  }
}