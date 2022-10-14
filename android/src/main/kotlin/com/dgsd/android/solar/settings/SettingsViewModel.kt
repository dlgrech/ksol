package com.dgsd.android.solar.settings

import android.app.Application
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.AndroidViewModel
import com.dgsd.android.solar.R
import com.dgsd.android.solar.applock.biometrics.AppLockBiometricManager
import com.dgsd.android.solar.applock.biometrics.BiometricPromptResult
import com.dgsd.android.solar.cluster.manager.ClusterManager
import com.dgsd.android.solar.common.clipboard.SystemClipboard
import com.dgsd.android.solar.common.model.SensitiveList
import com.dgsd.android.solar.extensions.getString
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.SimpleMutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.flow.call
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.android.solar.session.model.KeyPairSession
import com.dgsd.ksol.core.model.Cluster
import com.jakewharton.processphoenix.ProcessPhoenix
import kotlinx.coroutines.flow.map

class SettingsViewModel(
  application: Application,
  private val clusterManager: ClusterManager,
  private val sessionManager: SessionManager,
  private val biometricManager: AppLockBiometricManager,
  private val systemClipboard: SystemClipboard,
) : AndroidViewModel(application) {

  val activeClusterText = clusterManager.activeCluster.map { cluster ->
    when (cluster) {
      is Cluster.Custom -> cluster.rpcUrl
      Cluster.DEVNET -> getString(R.string.cluster_name_devnet)
      Cluster.MAINNET_BETA -> getString(R.string.cluster_name_mainnet_beta)
      Cluster.TESTNET -> getString(R.string.cluster_name_testnet)
    }
  }

  private val _showClusterPicker = MutableEventFlow<List<Cluster>>()
  val showClusterPicker = _showClusterPicker.asEventFlow()

  private val _showConfirmSignOut = SimpleMutableEventFlow()
  val showConfirmSignOut = _showConfirmSignOut.asEventFlow()

  private val _showBiometricAuthenticationPrompt = MutableEventFlow<BiometricPrompt.PromptInfo>()
  val showBiometricAuthenticationPrompt = _showBiometricAuthenticationPrompt.asEventFlow()

  private val _showSeedPhrase = MutableEventFlow<SensitiveList<String>>()
  val showSeedPhrase = _showSeedPhrase.asEventFlow()

  private val _showError = MutableEventFlow<CharSequence>()
  val showError = _showError.asEventFlow()

  private val _showSuccessMessage = MutableEventFlow<CharSequence>()
  val showSuccessMessage = _showSuccessMessage.asEventFlow()

  fun onSecretPhraseClicked() {
    if (biometricManager.isAvailableOnDevice()) {
      _showBiometricAuthenticationPrompt.tryEmit(
        biometricManager.createPrompt(
          title = getString(R.string.send_unlock_biometrics_title),
          description = getString(R.string.send_unlock_biometrics_description)
        )
      )
    } else {
      showSeedPhrase()
    }
  }

  fun onBiometricPromptResult(result: BiometricPromptResult) {
    if (result == BiometricPromptResult.SUCCESS) {
      sessionManager.upgradeSession()
      showSeedPhrase()
    }
  }

  fun onClusterClicked() {
    _showClusterPicker.tryEmit(
      listOf(Cluster.MAINNET_BETA, Cluster.TESTNET, Cluster.DEVNET)
    )
  }

  fun onCopySeedPhraseClicked(text: String) {
    systemClipboard.copy(text)
    _showSuccessMessage.tryEmit(getString(R.string.copied_to_clipboard))
  }

  fun onClusterSelected(cluster: Cluster) {
    clusterManager.setCluster(cluster)
    ProcessPhoenix.triggerRebirth(getApplication())
  }

  fun onSignOutClicked() {
    _showConfirmSignOut.call()
  }

  fun onSignOutConfirmed() {
    sessionManager.clear()
    ProcessPhoenix.triggerRebirth(getApplication())
  }

  private fun showSeedPhrase() {
    runCatching {
      (sessionManager.activeSession.value as KeyPairSession).seedInfo.seedPhrase
    }.onSuccess {
      _showSeedPhrase.tryEmit(it)
    }.onFailure {
      _showError.tryEmit(getString(R.string.settings_error_getting_secret_key))
    }
  }

}