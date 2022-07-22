package com.dgsd.android.solar.applock.biometrics

import android.app.Application
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt

class AppLockBiometricManagerImpl(
  private val application: Application,
) : AppLockBiometricManager {

  override fun isAvailableOnDevice(): Boolean {
    return BiometricManager.from(application)
      .canAuthenticate(VALID_AUTHENTICATORS) == BiometricManager.BIOMETRIC_SUCCESS
  }

  override fun createPrompt(
    title: CharSequence,
    description: CharSequence?
  ): BiometricPrompt.PromptInfo {
    return BiometricPrompt.PromptInfo.Builder()
      .setAllowedAuthenticators(VALID_AUTHENTICATORS)
      .setConfirmationRequired(false)
      .setTitle(title)
      .setDescription(description)
      .setNegativeButtonText(
        application.getString(android.R.string.cancel)
      )
      .build()
  }

  @Suppress("DEPRECATION")
  override fun createKeySpec(): KeyGenParameterSpec {
    val purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    return KeyGenParameterSpec.Builder(KEYSTORE_ALIAS, purposes)
      .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
      .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
      .setKeySize(KEY_SIZE)
      .setUserAuthenticationRequired(true)
      .setInvalidatedByBiometricEnrollment(true)
      .setIsStrongBoxBacked(true)
      .let {
        if (Build.VERSION.SDK_INT < 30) {
          it.setUserAuthenticationValidityDurationSeconds(AUTHENTICATION_VALIDITY_DURATION_SECONDS)
        } else {
          it.setUserAuthenticationParameters(
            AUTHENTICATION_VALIDITY_DURATION_SECONDS,
            KeyProperties.AUTH_BIOMETRIC_STRONG or KeyProperties.AUTH_DEVICE_CREDENTIAL
          )
        }
      }
      .build()
  }

  companion object {

    private const val KEY_SIZE = 256
    private const val AUTHENTICATION_VALIDITY_DURATION_SECONDS = 60
    private const val KEYSTORE_ALIAS = "solar_wallet_honeypot"
    private const val VALID_AUTHENTICATORS = BiometricManager.Authenticators.BIOMETRIC_STRONG
  }
}