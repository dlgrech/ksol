package com.dgsd.android.solar

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.applock.manager.AppLockManager
import com.dgsd.android.solar.deeplink.SolarDeeplinkingConstants
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.SimpleMutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.flow.call
import com.dgsd.android.solar.mobilewalletadapter.MobileWalletAdapterCoordinator
import com.dgsd.android.solar.mobilewalletadapter.MobileWalletAdapterCoordinatorFactory
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
  application: Application,
  private val sessionManager: SessionManager,
  private val appLockManager: AppLockManager,
  private val scenarioFactory: MobileWalletAdapterCoordinatorFactory,
  private val solPayLazy: Lazy<SolPay>,
) : AndroidViewModel(application) {

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
    object MobileWalletAdapterAuthorize: Destination
    data class SendWithSolPayRequest(val requestUrl: String) : Destination
    data class CompositeDestination(val destinations: List<Destination>) : Destination
    data class TransactionDetails(val signature: TransactionSignature) : Destination
  }

  private class IncomingDeeplinkInfo(
    val uri: Uri,
    val callingPackage: String?
  )

  private val _destination = MutableEventFlow<Destination>()
  val destination = _destination.asEventFlow()

  private val _close = SimpleMutableEventFlow()
  val close = _close.asEventFlow()

  var walletAdapterCoordinator: MobileWalletAdapterCoordinator? = null
  private set

  private var pendingDeeplinkAfterAppLock: IncomingDeeplinkInfo? = null

  fun onCreate() {
    sessionManager.activeSession
      .distinctUntilChangedBy { it.sessionId }
      .onEach { onSessionChanged(it) }
      .launchIn(viewModelScope)
  }

  fun onResume(deeplink: Uri?, callingPackage: String?) {
    if (sessionManager.activeSession.value is LockedAppSession) {
      pendingDeeplinkAfterAppLock = deeplink?.let { IncomingDeeplinkInfo(deeplink, callingPackage) }
    } else if (appLockManager.shouldShowAppLockEntry()) {
      pendingDeeplinkAfterAppLock = deeplink?.let { IncomingDeeplinkInfo(deeplink, callingPackage) }
      sessionManager.lockSession()
    } else if (deeplink != null) {
      maybeNavigateWithUri(IncomingDeeplinkInfo(deeplink, callingPackage))
    }
  }

  fun onNewIntent(uri: Uri?, callingPackage: String?) {
    if (uri != null) {
      maybeNavigateWithUri(IncomingDeeplinkInfo(uri, callingPackage))
    }
  }

  override fun onCleared() {
    super.onCleared()
    walletAdapterCoordinator?.close()
    walletAdapterCoordinator = null
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

  private fun maybeNavigateWithUri(incomingDeeplink: IncomingDeeplinkInfo): Boolean {
    if (sessionManager.activeSession.value !is WalletSession) {
      // We're not logged in
      return false
    }

    if (incomingDeeplink.uri.scheme == SolarDeeplinkingConstants.SCHEME) {
      if (maybeHandleAppSpecificDeeplink(incomingDeeplink)) {
        return true
      }
    }

    if (maybeHandleSolPayDeeplink(incomingDeeplink)) {
      return true
    }

    if (maybeHandleMobileWalletAdapterDeeplink(incomingDeeplink)) {
      return true
    }

    return false
  }

  private fun maybeHandleAppSpecificDeeplink(incomingDeeplink: IncomingDeeplinkInfo): Boolean {
    val destination = when (incomingDeeplink.uri.host) {
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

    return if (destination != null) {
      _destination.tryEmit(destination)
      true
    } else {
      false
    }
  }

  private fun maybeHandleSolPayDeeplink(incomingDeeplink: IncomingDeeplinkInfo): Boolean {
    val solPay = solPayLazy.value
    val solPayRequest = solPay.parseUrl(incomingDeeplink.uri.toString())
    if (solPayRequest == null) {
      return false
    } else {
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
  }

  private fun maybeHandleMobileWalletAdapterDeeplink(incomingDeeplink: IncomingDeeplinkInfo): Boolean {
    val coordinator =
      scenarioFactory.createFromUri(incomingDeeplink.uri, incomingDeeplink.callingPackage)
    walletAdapterCoordinator = coordinator
    if (coordinator != null) {
      onEach(coordinator.terminate) {
        _close.call()
      }

      onEach(coordinator.destination) { destination ->
        val appCoordinatorDestination = when (destination) {
          is MobileWalletAdapterCoordinator.Destination.Authorize -> {
            Destination.MobileWalletAdapterAuthorize
          }
        }

        _destination.tryEmit(appCoordinatorDestination)
      }

      coordinator.start()
      return true
    }

    return false
  }
}