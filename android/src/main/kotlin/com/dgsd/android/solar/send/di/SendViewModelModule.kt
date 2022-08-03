package com.dgsd.android.solar.send.di

import com.dgsd.android.solar.di.util.getScoped
import com.dgsd.android.solar.send.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module

object SendViewModelModule {

  fun create(): Module {
    return module {
      viewModel { params ->
        SendCoordinator(
          solPay = getScoped(),
          startingDestination = params.getOrNull(),
          solPayRequestUrl = params.getOrNull(),
        )
      }
      viewModelOf(::SendEnterAddressViewModel)
      viewModelOf(::SendEnterAmountViewModel)
      viewModelOf(::SendConfirmTransactionRequestViewModel)
      viewModelOf(::SendConfirmTransferRequestViewModel)
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