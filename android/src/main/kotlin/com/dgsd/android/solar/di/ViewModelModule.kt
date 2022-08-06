package com.dgsd.android.solar.di

import com.dgsd.android.solar.AppCoordinator
import com.dgsd.android.solar.applock.verification.AppEntryLockScreenViewModel
import com.dgsd.android.solar.di.util.getScoped
import com.dgsd.android.solar.home.HomeViewModel
import com.dgsd.android.solar.onboarding.di.OnboardingViewModelModule
import com.dgsd.android.solar.receive.di.ReceiveViewModelModule
import com.dgsd.android.solar.send.di.SendViewModelModule
import com.dgsd.android.solar.settings.SettingsViewModel
import com.dgsd.android.solar.transaction.details.TransactionDetailsViewModel
import com.dgsd.android.solar.transaction.list.TransactionListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module

internal object ViewModelModule {

    fun create(): Module {
        return module {
            viewModelOf(::SettingsViewModel)
            viewModelOf(::AppEntryLockScreenViewModel)

            viewModel {
                AppCoordinator(
                    sessionManager = get(),
                    appLockManager = get(),
                    solPayLazy = lazy { getScoped() }
                )
            }

            viewModel {
                TransactionListViewModel(
                    errorMessageFactory = get(),
                    solanaApiRepository = getScoped(),
                    transactionViewStateFactory = getScoped(),
                )
            }
            viewModel {
                TransactionDetailsViewModel(
                    session = getScoped(),
                    application = get(),
                    publicKeyFormatter = get(),
                    errorMessageFactory = get(),
                    solanaApiRepository = getScoped(),
                    systemClipboard = get(),
                    transactionViewStateFactory = getScoped(),
                    transactionSignature = get(),
                )
            }
            viewModel {
                HomeViewModel(
                    application = get(),
                    systemClipboard = get(),
                    errorMessageFactory = get(),
                    publicKeyFormatter = get(),
                    transactionViewStateFactory = getScoped(),
                    solanaApiRepository = getScoped(),
                    solPay = getScoped(),
                    nfcManager = get(),
                )
            }

            includes(OnboardingViewModelModule.create())
            includes(ReceiveViewModelModule.create())
            includes(SendViewModelModule.create())
        }
    }
}