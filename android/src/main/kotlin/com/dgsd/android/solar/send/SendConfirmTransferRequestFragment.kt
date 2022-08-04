package com.dgsd.android.solar.send

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.dgsd.android.solar.R
import com.dgsd.android.solar.di.util.parentViewModel
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.ksol.solpay.model.SolPayTransferRequest
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SendConfirmTransferRequestFragment : Fragment(R.layout.frag_send_confirm_transfer_request) {

  private val coordinator by parentViewModel<SendCoordinator>()
  private val viewModel by viewModel<SendConfirmTransferRequestViewModel> {
    parametersOf(
      checkNotNull(coordinator.solPayRequest as SolPayTransferRequest)
    )
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.requireViewById<Toolbar>(R.id.toolbar)
    val headerRecipient = view.requireViewById<TextView>(R.id.header_recipient)
    val headerAmount = view.requireViewById<TextView>(R.id.header_amount)
    val headerFee = view.requireViewById<TextView>(R.id.header_fee)
    val headerLabel = view.requireViewById<TextView>(R.id.header_label)
    val headerMessage = view.requireViewById<TextView>(R.id.header_message)
    val headerMemo = view.requireViewById<TextView>(R.id.header_memo)
    val valueRecipient = view.requireViewById<TextView>(R.id.value_recipient)
    val valueAmount = view.requireViewById<TextView>(R.id.value_amount)
    val valueFee = view.requireViewById<TextView>(R.id.value_fee)
    val valueLabel = view.requireViewById<TextView>(R.id.value_label)
    val valueMessage = view.requireViewById<TextView>(R.id.value_message)
    val valueMemo = view.requireViewById<TextView>(R.id.value_memo)
    val sendButton = view.requireViewById<View>(R.id.send)
    val submitLoadingIndicator = view.requireViewById<View>(R.id.submit_loading_indicator)

    toolbar.setNavigationOnClickListener {
      requireActivity().onBackPressed()
    }

    sendButton.setOnClickListener {
      viewModel.onSendClicked()
    }

    onEach(viewModel.recipientText) {
      headerRecipient.isVisible = it.isNotEmpty()
      valueRecipient.isVisible = it.isNotEmpty()
      valueRecipient.text = it
    }

    onEach(viewModel.amountText) {
      headerAmount.isVisible = it.isNotEmpty()
      valueAmount.isVisible = it.isNotEmpty()
      valueAmount.text = it
    }

    onEach(viewModel.feeText) {
      headerAmount.isVisible = it.isNotEmpty()
      valueAmount.isVisible = it.isNotEmpty()
      valueAmount.text = it
    }

    onEach(viewModel.messageText) {
      headerMessage.isVisible = !it.isNullOrEmpty()
      valueMessage.isVisible = !it.isNullOrEmpty()
      valueMessage.text = it
    }

    onEach(viewModel.labelText) {
      headerLabel.isVisible = !it.isNullOrEmpty()
      valueLabel.isVisible = !it.isNullOrEmpty()
      valueLabel.text = it
    }

    onEach(viewModel.memoText) {
      headerMemo.isVisible = !it.isNullOrEmpty()
      valueMemo.isVisible = !it.isNullOrEmpty()
      valueMemo.text = it
    }
  }
}