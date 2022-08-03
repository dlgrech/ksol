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

class SendEnterAddressFragment : Fragment(R.layout.frag_send_enter_address) {

  private val coordinator by parentViewModel<SendCoordinator>()
  private val viewModel by viewModel<SendEnterAddressViewModel>()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.requireViewById<Toolbar>(R.id.toolbar)
    val nextButton = view.requireViewById<View>(R.id.next)
    val addressInput = view.requireViewById<EditText>(R.id.input)
    val addressOnClipboardText = view.requireViewById<TextView>(R.id.address_on_clipboard)

    toolbar.setNavigationOnClickListener {
      requireActivity().onBackPressed()
    }

    nextButton.setOnClickListener {
      viewModel.onNextButtonClicked()
    }

    addressInput.doAfterTextChanged {
      viewModel.onTextChanged(it?.toString().orEmpty())
    }

    addressOnClipboardText.setOnClickListener {
      viewModel.onUseAddressOnClipboardClicked()
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.onCreate()
    }

    onEach(viewModel.navigateWithAddress) {
      coordinator.navigateWithAddressInput(it)
    }

    onEach(viewModel.addressOnClipboardText) {
      addressOnClipboardText.text = it
      addressOnClipboardText.isInvisible = it.isNullOrEmpty()
    }

    onEach(viewModel.errorMessage) {
      showModalFromErrorMessage(it)
    }
  }
}