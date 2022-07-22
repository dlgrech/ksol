package com.dgsd.android.solar.onboarding

import android.app.Application
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.AndroidViewModel
import com.dgsd.android.solar.R
import com.dgsd.android.solar.applock.biometrics.AppLockBiometricManager
import com.dgsd.android.solar.applock.biometrics.BiometricPromptResult
import com.dgsd.android.solar.extensions.getString
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.SimpleMutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.flow.call
import com.dgsd.android.solar.model.AccountSeedInfo
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.ksol.model.KeyPair

class OnboardingCoordinator(
  application: Application,
  private val sessionManager: SessionManager,
  private val biometricManager: AppLockBiometricManager,
) : AndroidViewModel(application) {

  sealed interface Destination {
    object Welcome : Destination
    object RestoreSeedPhraseFlow : Destination
    object SetupAppLock : Destination
    object CreateNewWalletFlow : Destination
  }

  private val _destination = MutableEventFlow<Destination>()
  val destination = _destination.asEventFlow()

  private val _showBiometricAuthenticationPrompt = MutableEventFlow<BiometricPrompt.PromptInfo>()
  val showBiometricAuthenticationPrompt = _showBiometricAuthenticationPrompt.asEventFlow()

  private val _showErrorPersistingSecrets = SimpleMutableEventFlow()
  val showErrorPersistingSecrets = _showErrorPersistingSecrets.asEventFlow()

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
    if (!biometricManager.isAvailableOnDevice()) {
      setActiveSessionWithSecrets()
    } else {
      _showBiometricAuthenticationPrompt.tryEmit(
        biometricManager.createPrompt(
          title = getString(R.string.onboarding_biometric_prompt_title),
          description = getString(R.string.onboarding_biometric_prompt_message),
        )
      )
    }
  }

  fun onBiometricPromptResult(result: BiometricPromptResult) {
    when (result) {
      BiometricPromptResult.SUCCESS -> setActiveSessionWithSecrets()
      BiometricPromptResult.FAIL -> _showErrorPersistingSecrets.call()
      BiometricPromptResult.CANCELLED -> {
        // No-op
      }
    }
  }

  fun onErrorPersistingSecretsModalDismissed() {
    sessionManager.setActiveSession(checkNotNull(keyPair).publicKey)
  }

  private fun setActiveSessionWithSecrets() {
    val seed = checkNotNull(seedInfo)
    val walletAccount = checkNotNull(keyPair)

    sessionManager.setActiveSession(seed, walletAccount)
  }
}