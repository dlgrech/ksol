package com.dgsd.android.solar.receive.requestamount

import androidx.fragment.app.Fragment
import com.dgsd.android.solar.R
import com.dgsd.android.solar.di.util.parentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RequestEnterAmountFragment : Fragment(R.layout.frag_receive_enter_amount) {

  private val coordinator by parentViewModel<RequestAmountCoordinator>()
  private val viewModel by viewModel<RequestEnterAmountViewModel>()
}