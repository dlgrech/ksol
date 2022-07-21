package com.dgsd.android.solar.onboarding

import androidx.lifecycle.ViewModel
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.model.AccountSeedInfo
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.ksol.model.KeyPair

class OnboardingCoordinator(
  private val sessionManager: SessionManager,
) : ViewModel() {

  sealed interface Destination {
    object Welcome : Destination
    object RestoreSeedPhraseFlow : Destination
    object SetupAppLock : Destination
    object CreateNewWalletFlow : Destination
  }

  private val _destination = MutableEventFlow<Destination>()
  val destination = _destination.asEventFlow()

  private var seedInfo: AccountSeedInfo? = null

  private var keyPair: KeyPair? = null

  fun onCreate() {
    _destination.tryEmit(Destination.Welcome)
  }

  fun navigateToAddFromSeedPhrase() {
    _destination.tryEmit(Destination.RestoreSeedPhraseFlow)
  }

  fun navigateToCreateNewAccount() {
    _destination.tryEmit(Destination.CreateNewWalletFlow)
  }

  fun navigateToSetupAppLock(
    seedInfo: AccountSeedInfo,
    keyPair: KeyPair,
  ) {
    this.seedInfo = seedInfo
    this.keyPair = keyPair

    _destination.tryEmit(Destination.SetupAppLock)
  }

  fun navigateFromAppLockSetup() {
    val seed = checkNotNull(seedInfo)
    val walletAccount = checkNotNull(keyPair)

    // TODO: Persist keypair + seed info

    sessionManager.setActiveSession(walletAccount.publicKey)
  }
}