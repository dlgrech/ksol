package com.dgsd.android.solar.send

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.di.util.parentViewModel
import com.dgsd.ksol.solpay.model.SolPayTransactionRequest
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SendConfirmTransactionRequestFragment : Fragment(R.layout.frag_send_confirm_transaction_request) {

  private val coordinator by parentViewModel<SendCoordinator>()
  private val viewModel by viewModel<SendConfirmTransactionRequestViewModel> {
    parametersOf(coordinator.solPayRequest as SolPayTransactionRequest)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.onCreate()
    }
  }
}