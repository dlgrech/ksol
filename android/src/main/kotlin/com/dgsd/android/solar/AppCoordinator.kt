package com.dgsd.android.solar

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.applock.manager.AppLockManager
import com.dgsd.android.solar.deeplink.SolarDeeplinkingConstants
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.android.solar.session.model.LockedAppSession
import com.dgsd.android.solar.session.model.NoActiveWalletSession
import com.dgsd.android.solar.session.model.Session
import com.dgsd.android.solar.session.model.WalletSession
import com.dgsd.ksol.model.TransactionSignature
import com.dgsd.ksol.solpay.SolPay
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AppCoordinator(
  private val sessionManager: SessionManager,
  private val appLockManager: AppLockManager,
  private val solPayLazy: Lazy<SolPay>,
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
    object SendWithNearby : Destination
    data class SendWithSolPayRequest(val requestUrl: String) : Destination
    data class CompositeDestination(val destinations: List<Destination>) : Destination
    data class TransactionDetails(val signature: TransactionSignature) : Destination
  }

  private val _destination = MutableEventFlow<Destination>()
  val destination = _destination.asEventFlow()


  private var pendingDeeplinkAfterAppLock: Uri? = null

  fun onCreate() {
    sessionManager.activeSession
      .distinctUntilChangedBy { it.sessionId }
      .onEach { onSessionChanged(it) }
      .launchIn(viewModelScope)
  }

  fun onResume(deeplink: Uri?) {
    if (sessionManager.activeSession.value is LockedAppSession) {
      pendingDeeplinkAfterAppLock = deeplink
    } else if (appLockManager.shouldShowAppLockEntry()) {
      pendingDeeplinkAfterAppLock = deeplink
      sessionManager.lockSession()
    } else if (deeplink != null) {
      maybeNavigateWithUri(deeplink)
    }
  }

  fun onNewIntent(uri: Uri?) {
    if (uri != null) {
      maybeNavigateWithUri(uri)
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
        val initialUri = pendingDeeplinkAfterAppLock
        if (initialUri == null || !maybeNavigateWithUri(initialUri)) {
          _destination.tryEmit(Destination.Home)
        }

        pendingDeeplinkAfterAppLock = null
      }
    }
  }

  private fun maybeNavigateWithUri(uri: Uri): Boolean {
    if (sessionManager.activeSession.value !is WalletSession) {
      // We're not logged in
      return false
    }

    if (uri.scheme == SolarDeeplinkingConstants.SCHEME) {
      val destination = when (uri.host) {
        SolarDeeplinkingConstants.DestinationHosts.SCAN_QR -> Destination.CompositeDestination(
          listOf(
            Destination.Home,
            Destination.SendWithQR
          )
        )

        SolarDeeplinkingConstants.DestinationHosts.RECEIVE_AMOUNT -> Destination.CompositeDestination(
          listOf(
            Destination.Home,
            Destination.RequestAmount
          )
        )
        SolarDeeplinkingConstants.DestinationHosts.YOUR_ADDRESS -> Destination.CompositeDestination(
          listOf(
            Destination.Home,
            Destination.ShareWalletAddress
          )
        )
        else -> null
      }

      if (destination != null) {
        _destination.tryEmit(destination)
        return true
      }
    }

    val solPay = solPayLazy.value
    val solPayRequest = solPay.parseUrl(uri.toString())
    if (solPayRequest != null) {
      _destination.tryEmit(
        Destination.CompositeDestination(
          listOf(
            Destination.Home,
            Destination.SendWithSolPayRequest(solPay.createUrl(solPayRequest))
          )
        )
      )

      return true
    }

    return false
  }
}