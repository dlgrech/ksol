package com.dgsd.android.solar.receive.requestamount

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.modalsheet.extensions.showModelFromErrorMessage
import com.dgsd.android.solar.di.util.parentViewModel
import com.dgsd.android.solar.extensions.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RequestAmountViewQRFragment : Fragment(R.layout.frag_request_amount_view_qr) {

  private val coordinator by parentViewModel<RequestAmountCoordinator>()
  private val viewModel by viewModel<RequestAmountViewQRViewModel> {
    parametersOf(checkNotNull(coordinator.lamports), coordinator.message)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.requireViewById<Toolbar>(R.id.toolbar)
    val qrCode = view.requireViewById<ImageView>(R.id.qr_code)
    val shareButton = view.requireViewById<View>(R.id.share)
    val doneButton = view.requireViewById<View>(R.id.done_button)

    toolbar.setNavigationOnClickListener {
      requireActivity().onBackPressed()
    }

    shareButton.setOnClickListener {
      viewModel.onShareClicked()
    }

    doneButton.setOnClickListener {
      coordinator.onCloseFlowClicked()
    }

    onEach(viewModel.qrCodeBitmap) {
      qrCode.setImageBitmap(it)
    }

    onEach(viewModel.showError) {
      showModelFromErrorMessage(it)
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.onCreate()
    }
  }
}