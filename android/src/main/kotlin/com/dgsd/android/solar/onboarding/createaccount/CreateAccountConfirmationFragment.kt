package com.dgsd.android.solar.onboarding.createaccount

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.modalsheet.extensions.showModalFromErrorMessage
import com.dgsd.android.solar.common.util.SwallowBackpressLifecycleObserver
import com.dgsd.android.solar.di.util.parentViewModel
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.extensions.showSnackbar
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

    val successTitle = view.requireViewById<View>(R.id.success_title)
    val explainerMessage = view.requireViewById<View>(R.id.explainer_message)
    val walletAddress = view.requireViewById<TextView>(R.id.wallet_address)
    val continueButton = view.requireViewById<Button>(R.id.continue_button)
    val loadingIndicator = view.requireViewById<View>(R.id.loading_indicator)

    continueButton.setOnClickListener {
      viewModel.onContinueClicked()
    }

    walletAddress.setOnClickListener {
      viewModel.onAddressClicked()
    }

    onEach(viewModel.publicKeyText) {
      walletAddress.text = it
    }

    onEach(viewModel.errorMessage) {
      showModalFromErrorMessage(it)
    }

    onEach(viewModel.showCopiedSuccessMessage) {
      showSnackbar(R.string.create_account_confirmation_copied_to_clipboard_success_message)
    }

    onEach(viewModel.showLoadingState) {
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