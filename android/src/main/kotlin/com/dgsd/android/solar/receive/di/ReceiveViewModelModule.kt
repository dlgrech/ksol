package com.dgsd.android.solar.receive.di

import com.dgsd.android.solar.di.util.getScoped
import com.dgsd.android.solar.receive.requestamount.RequestAmountCoordinator
import com.dgsd.android.solar.receive.requestamount.RequestAmountViewQRViewModel
import com.dgsd.android.solar.receive.requestamount.RequestEnterAmountViewModel
import com.dgsd.android.solar.receive.requestamount.RequestEnterMessageViewModel
import com.dgsd.android.solar.receive.shareaddress.ReceiveShareAddressViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module

object ReceiveViewModelModule {

  fun create(): Module {
    return module {
      viewModelOf(::RequestAmountCoordinator)
      viewModelOf(::RequestEnterAmountViewModel)
      viewModelOf(::RequestEnterMessageViewModel)
      viewModel {
        RequestAmountViewQRViewModel(
          application = get(),
          session = getScoped(),
          solPay = getScoped(),
          errorMessageFactory = get(),
          lamports = get(),
          message = get(),
        )
      }
      viewModel {
        ReceiveShareAddressViewModel(
          application = get(),
          session = getScoped(),
          publicKeyFormatter = get(),
          systemClipboard = get(),
          errorMessageFactory = get(),
        )
      }
    }
  }
}