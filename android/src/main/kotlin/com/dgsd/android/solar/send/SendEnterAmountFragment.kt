package com.dgsd.android.solar.send

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isInvisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.modalsheet.extensions.showModalFromErrorMessage
import com.dgsd.android.solar.di.util.parentViewModel
import com.dgsd.android.solar.extensions.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class SendEnterAmountFragment : Fragment(R.layout.frag_send_enter_amount) {

  private val coordinator by parentViewModel<SendCoordinator>()
  private val viewModel by viewModel<SendEnterAmountViewModel>()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.requireViewById<Toolbar>(R.id.toolbar)
    val nextButton = view.requireViewById<View>(R.id.next)
    val input = view.requireViewById<EditText>(R.id.input)
    val balanceText = view.requireViewById<TextView>(R.id.balance_text)

    toolbar.setNavigationOnClickListener {
      requireActivity().onBackPressed()
    }

    nextButton.setOnClickListener {
      viewModel.onNextButtonClicked()
    }

    input.doAfterTextChanged {
      viewModel.onInputChanged(it?.toString().orEmpty())
    }

    onEach(viewModel.balanceText) {
      balanceText.text = it
      balanceText.isInvisible = it.isEmpty()
    }

    onEach(viewModel.errorMessage) {
      showModalFromErrorMessage(it)
    }

    onEach(viewModel.isNextButtonEnabled) {
      nextButton.isEnabled = it
    }

    onEach(viewModel.continueWithLamports) {
      coordinator.navigateWithAmountInput(it)
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.onCreate()
    }
  }
}