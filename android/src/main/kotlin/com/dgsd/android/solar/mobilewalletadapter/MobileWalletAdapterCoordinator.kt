package com.dgsd.android.solar.mobilewalletadapter

import android.content.Context
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.SimpleMutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.flow.call
import com.solana.mobilewalletadapter.walletlib.scenario.AuthorizeRequest
import com.solana.mobilewalletadapter.walletlib.scenario.Scenario

class MobileWalletAdapterCoordinator internal constructor(
  internal val context: Context,
  private val scenario: Scenario,
  callbacks: MobileWalletAdapterScenarioCallbacks,
) {

  sealed interface Destination {
    data class Authorize(val request: AuthorizeRequest) : Destination
  }

  private val _destination = MutableEventFlow<Destination>()
  val destination = _destination.asEventFlow()

  private val _terminate = SimpleMutableEventFlow()
  val terminate = _terminate.asEventFlow()

  var authorizationRequest: AuthorizeRequest? = null
    private set

  init {
    callbacks.attach(this)
  }

  fun start() {
    scenario.start()
  }

  fun close() {
    scenario.close()
  }

  internal fun navigateToAuthorizationRequest(request: AuthorizeRequest) {
    authorizationRequest = request
    _destination.tryEmit(Destination.Authorize(request))
  }

  internal fun onTeardownComplete() {
    _terminate.call()
  }
}