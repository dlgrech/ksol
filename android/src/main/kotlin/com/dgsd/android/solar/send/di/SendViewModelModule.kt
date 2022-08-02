package com.dgsd.android.solar.send.di

import com.dgsd.android.solar.send.SendCoordinator
import com.dgsd.android.solar.send.SendEnterAddressViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module

object SendViewModelModule {

  fun create(): Module {
    return module {
      viewModelOf(::SendCoordinator)
      viewModelOf(::SendEnterAddressViewModel)
    }
  }
}