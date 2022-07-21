package com.dgsd.android.solar.applock.setup

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.util.KeyboardInputUtils
import com.dgsd.android.solar.di.util.parentViewModel
import com.dgsd.android.solar.extensions.getColorAttr
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.widget.keyboard.NumericKeyboardView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SetupAppLockConfirmPinFragment : Fragment(R.layout.frag_setup_app_lock_confirm_pin) {

  private val coordinator: SetupAppLockCoordinator by parentViewModel()
  private val viewModel: SetupAppLockConfirmPinViewModel by viewModel {
    parametersOf(checkNotNull(coordinator.enteredPin))
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
    val keyboard = view.findViewById<NumericKeyboardView>(R.id.keyboard)
    val codeDisplay = view.findViewById<TextView>(R.id.code)
    val confirmButton = view.findViewById<View>(R.id.confirm)

    toolbar.setNavigationOnClickListener {
      requireActivity().onBackPressed()
    }

    confirmButton.setOnClickListener {
      viewModel.onConfirmClicked()
    }

    KeyboardInputUtils.setup(keyboard, codeDisplay) { code ->
      viewModel.onCodeChanged(code)
    }

    onEach(viewModel.showAsError) { showAsError ->
      codeDisplay.setTextColor(
        if (showAsError) {
          requireContext().getColorAttr(R.attr.colorError)
        } else {
          requireContext().getColorAttr(android.R.attr.textColorPrimary)
        }
      )
    }

    onEach(viewModel.showError) { message ->
      Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    onEach(viewModel.continueWithCode) {
      coordinator.navigateFromConfirmPin()
    }
  }
}