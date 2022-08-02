package com.dgsd.android.solar.receive.requestamount

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.dgsd.android.solar.R
import com.dgsd.android.solar.di.util.parentViewModel
import com.dgsd.android.solar.extensions.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class RequestEnterMessageFragment : Fragment(R.layout.frag_receive_enter_message) {

  private val coordinator by parentViewModel<RequestAmountCoordinator>()
  private val viewModel by viewModel<RequestEnterMessageViewModel>()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.requireViewById<Toolbar>(R.id.toolbar)
    val inputField = view.requireViewById<EditText>(R.id.message_input)
    val nextButton = view.requireViewById<View>(R.id.next)

    toolbar.setNavigationOnClickListener {
      requireActivity().onBackPressed()
    }

    inputField.doAfterTextChanged {
      viewModel.onInputChanged(it?.toString().orEmpty())
    }

    nextButton.setOnClickListener {
      viewModel.onNextButtonClicked()
    }

    onEach(viewModel.continueWithMessage) {
      coordinator.onMessageEntered(it)
    }
  }
}