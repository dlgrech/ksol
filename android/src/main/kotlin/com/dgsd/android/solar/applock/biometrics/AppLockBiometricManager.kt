package com.dgsd.android.solar.applock.biometrics

import android.security.keystore.KeyGenParameterSpec
import androidx.biometric.BiometricPrompt

interface AppLockBiometricManager {

  fun isAvailableOnDevice(): Boolean

  fun createKeySpec(): KeyGenParameterSpec

  fun createPrompt(
    title: CharSequence,
    description: CharSequence? = null
  ): BiometricPrompt.PromptInfo
}