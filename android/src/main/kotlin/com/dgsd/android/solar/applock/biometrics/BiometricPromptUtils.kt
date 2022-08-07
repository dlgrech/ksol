package com.dgsd.android.solar.applock.biometrics

import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun Fragment.showBiometricPrompt(
  promptInfo: BiometricPrompt.PromptInfo,
) = suspendCancellableCoroutine<BiometricPromptResult> { continuation ->
  val prompt = BiometricPrompt(
    this,
    object : BiometricPrompt.AuthenticationCallback() {
      override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        if (!continuation.isCompleted) {
          continuation.resume(BiometricPromptResult.SUCCESS)
        }
      }

      override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        if (!continuation.isCompleted) {
          val result = when (errorCode) {
            BiometricPrompt.ERROR_CANCELED,
            BiometricPrompt.ERROR_TIMEOUT,
            BiometricPrompt.ERROR_USER_CANCELED,
            BiometricPrompt.ERROR_NEGATIVE_BUTTON -> BiometricPromptResult.CANCELLED
            else -> BiometricPromptResult.FAIL
          }

          continuation.resume(result)
        }
      }

      override fun onAuthenticationFailed() {
        if (!continuation.isCompleted) {
          continuation.resume(BiometricPromptResult.FAIL)
        }
      }
    }
  )

  prompt.authenticate(promptInfo)
}