package com.dgsd.android.solar.send.di

import com.dgsd.android.solar.di.util.getScoped
import com.dgsd.android.solar.send.SendCoordinator
import com.dgsd.android.solar.send.SendEnterAddressViewModel
import com.dgsd.android.solar.send.SendScanQRViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module

object SendViewModelModule {

  fun create(): Module {
    return module {
      viewModelOf(::SendCoordinator)
      viewModelOf(::SendEnterAddressViewModel)
      viewModel {
        SendScanQRViewModel(
          application = get(),
          permissionsManager = get(),
          solPay = getScoped(),
        )
      }
    }
  }
}