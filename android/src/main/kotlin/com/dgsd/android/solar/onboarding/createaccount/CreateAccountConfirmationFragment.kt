package com.dgsd.android.solar.onboarding.createaccount

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.util.SwallowBackpressLifecycleObserver
import com.dgsd.android.solar.di.util.parentViewModel
import com.dgsd.android.solar.extensions.onEach
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CreateAccountConfirmationFragment : Fragment(R.layout.frag_create_account_confirmation) {

  private val createAccountCoordinator: CreateAccountCoordinator by parentViewModel()
  private val viewModel: CreateAccountConfirmationViewModel by viewModel {
    parametersOf(createAccountCoordinator.seedInfo)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    SwallowBackpressLifecycleObserver.attach(this)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val successTitle = view.findViewById<View>(R.id.success_title)
    val explainerMessage = view.findViewById<View>(R.id.explainer_message)
    val walletAddress = view.findViewById<TextView>(R.id.wallet_address)
    val continueButton = view.findViewById<Button>(R.id.continue_button)
    val loadingIndicator = view.findViewById<View>(R.id.loading_indicator)

    continueButton.setOnClickListener {
      viewModel.onContinueClicked()
    }

    walletAddress.setOnClickListener {
      viewModel.onAddressClicked()
    }

    onEach(viewModel.publicKeyText) {
      walletAddress.text = it
    }

    onEach(viewModel.showCopiedSuccessMessage) {
      Snackbar.make(
        view,
        R.string.create_account_confirmation_copied_to_clipboard_success_message,
        Snackbar.LENGTH_SHORT
      ).show()
    }

    onEach(viewModel.isLoading) {
      loadingIndicator.isVisible = it
      successTitle.isInvisible = it
      explainerMessage.isInvisible = it
      walletAddress.isInvisible = it
      continueButton.isInvisible = it
    }

    onEach(viewModel.continueWithFlow) { keyPair ->
      createAccountCoordinator.onWalletAccountCreated(keyPair)
    }
  }
}