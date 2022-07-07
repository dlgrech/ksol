package com.dgsd.android.solar.onboarding.di

import com.dgsd.android.solar.onboarding.CreateNewAccountViewModel
import com.dgsd.android.solar.onboarding.OnboardingCoordinator
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object OnboardingViewModelModule {

    fun create(): Module {
        return module {
            viewModel<OnboardingCoordinator>()
            viewModel<CreateNewAccountViewModel>()
        }
    }
}