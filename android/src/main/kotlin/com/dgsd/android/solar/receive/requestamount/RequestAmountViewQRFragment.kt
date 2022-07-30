package com.dgsd.android.solar.receive.requestamount

import androidx.fragment.app.Fragment
import com.dgsd.android.solar.R
import com.dgsd.android.solar.di.util.parentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RequestAmountViewQRFragment : Fragment(R.layout.frag_request_amount_view_qr) {

  private val coordinator by parentViewModel<RequestAmountCoordinator>()
  private val viewModel by viewModel<RequestAmountViewQRViewModel> {
    parametersOf(checkNotNull(coordinator.lamports))
  }
}