package com.dgsd.android.solar.applock.verification

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dgsd.android.solar.R
import com.dgsd.android.solar.applock.biometrics.showBiometricPrompt
import com.dgsd.android.solar.common.modalsheet.extensions.showModalFromErrorMessage
import com.dgsd.android.solar.common.util.KeyboardInputUtils
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.widget.keyboard.NumericKeyboardView
import org.koin.androidx.viewmodel.ext.android.viewModel

class AppEntryLockScreenFragment : Fragment(R.layout.frag_app_lock) {

  private val viewModel by viewModel<AppEntryLockScreenViewModel>()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val keyboard = view.requireViewById<NumericKeyboardView>(R.id.keyboard)
    val codeDisplay = view.requireViewById<TextView>(R.id.code)
    val unlockButton = view.requireViewById<View>(R.id.unlock)

    unlockButton.setOnClickListener {
      viewModel.onUnlockClicked()
    }

    keyboard.setOnCustomActionPressed {
      viewModel.onUseBiometricsClicked()
    }

    KeyboardInputUtils.setup(keyboard, codeDisplay) { code ->
      viewModel.onCodeChanged(code)
    }

    onEach(viewModel.isBiometricsEnabled) {
      keyboard.setCustomActionImage(
        if (it) {
          ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_fingerprint_24)
        } else {
          null
        }
      )
    }

    onEach(viewModel.showError) { message ->
      showModalFromErrorMessage(message)
    }

    onEach(viewModel.isUnlockButtonEnabled) {
      unlockButton.isEnabled = it
    }

    onEach(viewModel.showBiometricAuthenticationPrompt) { promptInfo ->
      val result = showBiometricPrompt(promptInfo)
      viewModel.onBiometricPromptResult(result)
    }
  }

  companion object {

    fun newInstance(): AppEntryLockScreenFragment {
      return AppEntryLockScreenFragment()
    }
  }
}