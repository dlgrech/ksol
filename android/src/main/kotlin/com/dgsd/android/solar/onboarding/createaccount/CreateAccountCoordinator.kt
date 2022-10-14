package com.dgsd.android.solar.onboarding.createaccount

import androidx.lifecycle.ViewModel
import com.dgsd.android.solar.common.model.SensitiveList
import com.dgsd.android.solar.common.model.SensitiveString
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.model.AccountSeedInfo
import com.dgsd.ksol.core.model.KeyPair

class CreateAccountCoordinator : ViewModel() {

  sealed interface Destination {
    object EnterPassphrase : Destination
    object ViewSeedPhrase : Destination
    object Confirmation : Destination
  }

  private val _destination = MutableEventFlow<Destination>()
  val destination = _destination.asEventFlow()

  private val _continueWithFlow = MutableEventFlow<Pair<AccountSeedInfo, KeyPair>>()
  val continueWithFlow = _continueWithFlow.asEventFlow()

  var passphrase: SensitiveString? = null
    private set

  var seedPhrase: SensitiveList<String>? = null
    private set

  var createdWallet: KeyPair? = null
    private set

  val seedInfo: AccountSeedInfo?
    get() {
      return if (passphrase == null || seedPhrase == null) {
        null
      } else {
        AccountSeedInfo(checkNotNull(seedPhrase), checkNotNull(passphrase))
      }
    }

  fun onCreate() {
    _destination.tryEmit(Destination.EnterPassphrase)
  }

  fun onPassphraseConfirmed(passphrase: SensitiveString?) {
    this.passphrase = passphrase
    _destination.tryEmit(Destination.ViewSeedPhrase)
  }

  fun onSeedPhraseConfirmed(seedPhrase: SensitiveList<String>) {
    this.seedPhrase = seedPhrase
    _destination.tryEmit(Destination.Confirmation)
  }

  fun onWalletAccountCreated(keyPair: KeyPair) {
    createdWallet = keyPair

    _continueWithFlow.tryEmit(checkNotNull(seedInfo) to keyPair)
  }
}