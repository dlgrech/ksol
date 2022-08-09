package com.dgsd.android.solar.mobilewalletadapter.di

import com.dgsd.android.solar.di.util.getScoped
import com.dgsd.android.solar.mobilewalletadapter.authorize.MobileWalletAdapterAuthorizeViewModel
import com.dgsd.android.solar.mobilewalletadapter.signandsendtransactions.MobileWalletAdapterSignAndSendTransactionViewModel
import com.dgsd.android.solar.mobilewalletadapter.signmessages.MobileWalletAdapterSignMessagesViewModel
import com.dgsd.android.solar.mobilewalletadapter.signtransactions.MobileWalletAdapterSignTransactionViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object MobileWalletAdapterViewModelModule {

  fun create(): Module {
    return module {
      viewModel { params ->
        MobileWalletAdapterAuthorizeViewModel(
          application = get(),
          session = getScoped(),
          authorizeRequest = params.get(),
          callingPackage = params.get(),
          authorityManager = get()
        )
      }

      viewModel {
        MobileWalletAdapterSignTransactionViewModel(
          application = get(),
          signTransactionsRequest = get(),
          sessionManager = get(),
          publicKeyFormatter = get(),
          biometricManager = get(),
        )
      }

      viewModel {
        MobileWalletAdapterSignMessagesViewModel(
          application = get(),
          signMessagesRequest = get(),
          sessionManager = get(),
          biometricManager = get(),
        )
      }

      viewModel {
        MobileWalletAdapterSignAndSendTransactionViewModel(
          application = get(),
          signAndSendTransactionsRequest = get(),
          sessionManager = get(),
          publicKeyFormatter = get(),
          biometricManager = get(),
          solanaApiRepository = getScoped(),
        )
      }
    }
  }
}