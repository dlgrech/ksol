package com.dgsd.android.solar.mobilewalletadapter

import com.dgsd.ksol.LocalTransactions
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

  override fun onReauthorizeRequest(request: ReauthorizeRequest) {
    coordinator.navigateWithReauthorizationRequest(request)
  }

  override fun onSignTransactionsRequest(request: SignTransactionsRequest) {
    coordinator.navigateWithSignTransactionsRequest(request)
  }

  override fun onSignMessagesRequest(request: SignMessagesRequest) {
    coordinator.navigateWithSignMessagesRequest(request)
  }

  override fun onSignAndSendTransactionsRequest(request: SignAndSendTransactionsRequest) {
    coordinator.navigateWithSignAndSendTransactionsRequest(request)
  }
}