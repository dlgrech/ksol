package com.dgsd.android.solar.mobilewalletadapter

import com.solana.mobilewalletadapter.walletlib.scenario.*

internal class MobileWalletAdapterScenarioCallbacks : EmptyScenarioCallbacks() {

  private lateinit var coordinator: MobileWalletAdapterCoordinator

  fun attach(scenario: MobileWalletAdapterCoordinator) {
    coordinator = scenario
  }

  override fun onScenarioServingComplete() {
    coordinator.close()
  }

  override fun onScenarioTeardownComplete() {
    coordinator.onTeardownComplete()
  }

  override fun onAuthorizeRequest(request: AuthorizeRequest) {
    coordinator.navigateToAuthorizationRequest(request)
  }

  override fun onScenarioError() {
    println("HERE: onScenarioError()")
  }

  override fun onReauthorizeRequest(request: ReauthorizeRequest) {
    coordinator.navigateWithReauthorizationRequest(request)
  }

  override fun onSignTransactionsRequest(request: SignTransactionsRequest) {
    println("HERE: onSignTransactionsRequest(): $request")
  }

  override fun onSignMessagesRequest(request: SignMessagesRequest) {
    println("HERE: onSignMessagesRequest() $request")
  }

  override fun onSignAndSendTransactionsRequest(request: SignAndSendTransactionsRequest) {
    println("HERE: onSignAndSendTransactionsRequest(): $request")
  }
}