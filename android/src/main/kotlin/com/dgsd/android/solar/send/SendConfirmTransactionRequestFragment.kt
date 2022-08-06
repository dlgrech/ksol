package com.dgsd.android.solar.send

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.applock.biometrics.showBiometricPrompt
import com.dgsd.android.solar.common.modalsheet.extensions.showModalFromErrorMessage
import com.dgsd.android.solar.di.util.parentViewModel
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.extensions.setUrl
import com.dgsd.ksol.solpay.model.SolPayTransactionRequest
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SendConfirmTransactionRequestFragment :
  Fragment(R.layout.frag_send_confirm_transaction_request) {

  private val coordinator by parentViewModel<SendCoordinator>()
  private val viewModel by viewModel<SendConfirmTransactionRequestViewModel> {
    parametersOf(coordinator.solPayRequest as SolPayTransactionRequest)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.requireViewById<Toolbar>(R.id.toolbar)
    val logo = view.requireViewById<ImageView>(R.id.logo)
    val label = view.requireViewById<TextView>(R.id.label)
    val headerMessage = view.requireViewById<TextView>(R.id.header_message)
    val valueMessage = view.requireViewById<TextView>(R.id.value_message)
    val headerRecipient = view.requireViewById<TextView>(R.id.header_recipient)
    val valueRecipient = view.requireViewById<TextView>(R.id.value_recipient)
    val headerAmount = view.requireViewById<TextView>(R.id.header_amount)
    val valueAmount = view.requireViewById<TextView>(R.id.value_amount)
    val valueFee = view.requireViewById<TextView>(R.id.value_fee)
    val shimmerValueFee = view.requireViewById<View>(R.id.value_fee_shimmer)
    val sendLoadingIndicator = view.requireViewById<View>(R.id.submit_loading_indicator)
    val sendButton = view.requireViewById<View>(R.id.send)
    val shimmerSendButton = view.requireViewById<View>(R.id.shimmer_send)
    val cardContent = view.requireViewById<View>(R.id.card_content)
    val shimmerCardContent = view.requireViewById<View>(R.id.shimmer_card_content)

    toolbar.setNavigationOnClickListener {
      requireActivity().onBackPressed()
    }

    sendButton.setOnClickListener {
      viewModel.onSendClicked()
    }

    onEach(viewModel.logoUrl) {
      if (it.isNullOrEmpty()) {
        logo.isVisible = false
      } else {
        logo.isVisible = true
        logo.setUrl(it)
      }
    }

    onEach(viewModel.message) {
      headerMessage.isVisible = !it.isNullOrEmpty()
      valueMessage.isVisible = !it.isNullOrEmpty()
      valueMessage.text = it
    }

    onEach(viewModel.label) {
      label.isVisible = !it.isNullOrEmpty()
      label.text = it
    }

    onEach(viewModel.recipientText) {
      headerRecipient.isVisible = !it.isNullOrEmpty()
      valueRecipient.isVisible = !it.isNullOrEmpty()
      valueRecipient.text = it
    }

    onEach(viewModel.amountText) {
      headerAmount.isVisible = !it.isNullOrEmpty()
      valueAmount.isVisible = !it.isNullOrEmpty()
      valueAmount.text = it
    }

    onEach(viewModel.feeText) {
      valueFee.text = it
    }

    onEach(viewModel.isLoading) {
      shimmerCardContent.isVisible = it
      cardContent.isVisible = !it

      shimmerSendButton.isInvisible = !it
      sendButton.isInvisible = it
    }

    onEach(viewModel.isLoadingFee) {
      valueFee.isVisible = !it
      shimmerValueFee.isVisible = it
    }

    onEach(viewModel.continueWithTransactionSignature) {
      coordinator.navigateWithTransactionSignature(it)
    }

    onEach(viewModel.showBiometricAuthenticationPrompt) {
      val result = showBiometricPrompt(it)
      viewModel.onBiometricPromptResult(result)
    }

    onEach(viewModel.showError) {
      showModalFromErrorMessage(it)
    }

    onEach(viewModel.isSubmitTransactionLoading) {
      sendLoadingIndicator.isInvisible = !it
      sendButton.isInvisible = it
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.onCreate()
    }
  }
}