package com.dgsd.android.solar.onboarding.restoreaccount

import androidx.lifecycle.ViewModel
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.model.AccountSeedInfo
import com.dgsd.ksol.core.model.KeyPair

class RestoreAccountCoordinator : ViewModel() {

  sealed interface Destination {
    object EnterSeedPhrase : Destination
    object SelectAccount : Destination
  }

  private val _destination = MutableEventFlow<Destination>()
  val destination = _destination.asEventFlow()

  private val _continueWithFlow = MutableEventFlow<Pair<AccountSeedInfo, KeyPair>>()
  val continueWithFlow = _continueWithFlow.asEventFlow()

  var seedInfo: AccountSeedInfo? = null
    private set

  var selectedWallet: KeyPair? = null
    private set

  fun onCreate() {
    _destination.tryEmit(Destination.EnterSeedPhrase)
  }

  fun onSeedGenerated(seedInfo: AccountSeedInfo) {
    this.seedInfo = seedInfo

    _destination.tryEmit(Destination.SelectAccount)
  }

  fun onWalletSelected(keyPair: KeyPair) {
    selectedWallet = keyPair
    _continueWithFlow.tryEmit(checkNotNull(seedInfo) to checkNotNull(selectedWallet))
  }
}