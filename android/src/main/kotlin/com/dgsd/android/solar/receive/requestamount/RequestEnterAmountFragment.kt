package com.dgsd.android.solar.receive.requestamount

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.transition.TransitionManager
import com.dgsd.android.solar.R
import com.dgsd.android.solar.di.util.parentViewModel
import com.dgsd.android.solar.extensions.getColorAttr
import com.dgsd.android.solar.extensions.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class RequestEnterAmountFragment : Fragment(R.layout.frag_receive_enter_amount) {

  private val coordinator by parentViewModel<RequestAmountCoordinator>()
  private val viewModel by viewModel<RequestEnterAmountViewModel>()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.requireViewById<Toolbar>(R.id.toolbar)
    val inputField = view.requireViewById<EditText>(R.id.amount_input)
    val nextButton = view.requireViewById<View>(R.id.next)
    val errorMessage = view.requireViewById<TextView>(R.id.error_message)

    toolbar.setNavigationOnClickListener {
      requireActivity().onBackPressed()
    }

    inputField.doAfterTextChanged {
      viewModel.onInputChanged(it?.toString().orEmpty())
    }

    nextButton.setOnClickListener {
      viewModel.onNextButtonClicked()
    }

    onEach(viewModel.isNextButtonEnabled) {
      nextButton.isEnabled = it
    }

    onEach(viewModel.continueWithLamports) {
      coordinator.onAmountEntered(it)
    }

    onEach(viewModel.errorMessage) {
      TransitionManager.beginDelayedTransition(view as ViewGroup)
      errorMessage.text = it

      if (it.isNullOrEmpty()) {
        errorMessage.isVisible = false
        inputField.backgroundTintList = null
        inputField.setTextColor(requireContext().getColorAttr(android.R.attr.textColorPrimary))
      } else {
        errorMessage.isVisible = true
        inputField.backgroundTintList = ColorStateList.valueOf(
          requireContext().getColorAttr(R.attr.colorError)
        )
        inputField.setTextColor(requireContext().getColorAttr(R.attr.colorError))
      }
    }
  }
}