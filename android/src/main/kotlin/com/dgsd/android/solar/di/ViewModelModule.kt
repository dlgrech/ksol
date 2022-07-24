package com.dgsd.android.solar.di

import com.dgsd.android.solar.AppCoordinator
import com.dgsd.android.solar.di.util.getScoped
import com.dgsd.android.solar.home.HomeViewModel
import com.dgsd.android.solar.onboarding.di.OnboardingViewModelModule
import com.dgsd.android.solar.receive.ReceiveViewModel
import com.dgsd.android.solar.settings.SettingsViewModel
import com.dgsd.android.solar.transaction.TransactionDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module

internal object ViewModelModule {

    fun create(): Module {
        return module {
            viewModelOf(::AppCoordinator)
            viewModelOf(::SettingsViewModel)
            viewModelOf(::ReceiveViewModel)
            viewModelOf(::TransactionDetailsViewModel)
            viewModel {
                HomeViewModel(
                    application = get(),
                    transactionViewStateFactory = getScoped(),
                    solanaApiRepository = getScoped(),
                    nfcManager = get(),
                )
            }

            includes(OnboardingViewModelModule.create())
        }
    }
}