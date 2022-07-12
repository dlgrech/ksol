package com.dgsd.android.solar.di

import com.dgsd.android.solar.AppCoordinator
import com.dgsd.android.solar.di.util.getScoped
import com.dgsd.android.solar.home.HomeViewModel
import com.dgsd.android.solar.onboarding.di.OnboardingViewModelModule
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module

internal object ViewModelModule {

    fun create(): Module {
        return module {
            viewModelOf(::AppCoordinator)
            viewModel {
                HomeViewModel(
                    solanaApiRepository = getScoped(),
                )
            }

            includes(OnboardingViewModelModule.create())
        }
    }
}