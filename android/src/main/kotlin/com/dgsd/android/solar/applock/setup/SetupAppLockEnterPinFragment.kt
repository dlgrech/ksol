package com.dgsd.android.solar.applock.setup

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.util.KeyboardInputUtils
import com.dgsd.android.solar.common.util.SwallowBackpressLifecycleObserver
import com.dgsd.android.solar.di.util.parentViewModel
import com.dgsd.android.solar.extensions.getColorAttr
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.extensions.showSnackbar
import com.dgsd.android.solar.widget.keyboard.NumericKeyboardView
import org.koin.androidx.viewmodel.ext.android.viewModel

class SetupAppLockEnterPinFragment : Fragment(R.layout.frag_setup_app_lock_enter_pin) {

  private val coordinator: SetupAppLockCoordinator by parentViewModel()
  private val viewModel: SetupAppLockEnterPinViewModel by viewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    SwallowBackpressLifecycleObserver.attach(this)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val keyboard = view.requireViewById<NumericKeyboardView>(R.id.keyboard)
    val codeDisplay = view.requireViewById<TextView>(R.id.code)
    val nextButton = view.requireViewById<View>(R.id.next)

    nextButton.setOnClickListener {
      viewModel.onNextClicked()
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
      showSnackbar(message)
    }

    onEach(viewModel.continueWithCode) { code ->
      coordinator.navigateFromEnterPin(code)
    }
  }
}