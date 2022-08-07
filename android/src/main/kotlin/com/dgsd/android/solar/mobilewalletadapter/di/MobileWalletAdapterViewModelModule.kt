package com.dgsd.android.solar.mobilewalletadapter.di

import com.dgsd.android.solar.di.util.getScoped
import com.dgsd.android.solar.mobilewalletadapter.authorize.MobileWalletAdapterAuthorizeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object MobileWalletAdapterViewModelModule {

  fun create(): Module {
    return module {
      viewModel {
        MobileWalletAdapterAuthorizeViewModel(
          application = get(),
          session = getScoped(),
          authorizeRequest = get(),
          clusterManager = get(),
        )
      }
    }
  }
}