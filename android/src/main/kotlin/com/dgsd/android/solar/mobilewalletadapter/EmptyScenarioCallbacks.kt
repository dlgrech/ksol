package com.dgsd.android.solar.mobilewalletadapter

import com.solana.mobilewalletadapter.walletlib.scenario.*

/**
 * [Scenario.Callbacks] implementation with default, empty implementations of each method
 */
abstract class EmptyScenarioCallbacks : Scenario.Callbacks {
  override fun onScenarioReady() = Unit
  override fun onScenarioServingClients() = Unit
  override fun onScenarioServingComplete() = Unit
  override fun onScenarioComplete() = Unit
  override fun onScenarioError() = Unit
  override fun onScenarioTeardownComplete() = Unit
  override fun onAuthorizeRequest(request: AuthorizeRequest) = Unit
  override fun onReauthorizeRequest(request: ReauthorizeRequest) = Unit
  override fun onSignTransactionsRequest(request: SignTransactionsRequest) = Unit
  override fun onSignMessagesRequest(request: SignMessagesRequest) = Unit
  override fun onSignAndSendTransactionsRequest(request: SignAndSendTransactionsRequest) = Unit
}