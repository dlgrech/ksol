package com.dgsd.android.solar.applock.verification

import android.app.Application
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.AndroidViewModel
import com.dgsd.android.solar.R
import com.dgsd.android.solar.applock.biometrics.AppLockBiometricManager
import com.dgsd.android.solar.applock.biometrics.BiometricPromptResult
import com.dgsd.android.solar.applock.manager.AppLockManager
import com.dgsd.android.solar.common.model.SensitiveString
import com.dgsd.android.solar.common.util.stateFlowOf
import com.dgsd.android.solar.extensions.getString
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.android.solar.session.model.LockedAppSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class AppEntryLockScreenViewModel(
  application: Application,
  private val sessionManager: SessionManager,
  private val biometricManager: AppLockBiometricManager,
  private val appLockManager: AppLockManager,
) : AndroidViewModel(application) {

  private val inputtedCode = MutableStateFlow(SensitiveString(""))

  private val _showError = MutableEventFlow<CharSequence>()
  val showError = _showError.asEventFlow()

  val isUnlockButtonEnabled = inputtedCode.map { it.sensitiveValue.isNotEmpty() }

  val isBiometricsEnabled = stateFlowOf {
    biometricManager.isAvailableOnDevice()
  }

  private val _showBiometricAuthenticationPrompt = MutableEventFlow<BiometricPrompt.PromptInfo>()
  val showBiometricAuthenticationPrompt = _showBiometricAuthenticationPrompt.asEventFlow()

  fun onUnlockClicked() {
    if (appLockManager.attemptUnlock(inputtedCode.value)) {
      continueAfterUnlock()
    } else {
      _showError.tryEmit(
        getString(R.string.app_lock_entry_error_incorrect_code)
      )
    }
  }

  fun onUseBiometricsClicked() {
    _showBiometricAuthenticationPrompt.tryEmit(
      biometricManager.createPrompt(
        title = getString(R.string.app_lock_entry_biometric_prompt_title),
        description = getString(R.string.app_lock_entry_biometric_prompt_message),
      )
    )
  }

  fun onCodeChanged(code: String) {
    inputtedCode.value = SensitiveString(code)
  }


  fun onBiometricPromptResult(result: BiometricPromptResult) {
    if (result == BiometricPromptResult.SUCCESS) {
      appLockManager.unlock()
      continueAfterUnlock()
    } else if (result == BiometricPromptResult.FAIL) {
      _showError.tryEmit(
        getString(R.string.app_lock_entry_error_invalid_biometrics)
      )
    }
  }

  private fun continueAfterUnlock() {
    val session = sessionManager.activeSession.value
    if (session is LockedAppSession) {
      sessionManager.setActiveSession(session.publicKey)
    }
  }
}