package com.dgsd.android.solar.onboarding.di

import com.dgsd.android.solar.onboarding.createaccount.CreateAccountViewSeedPhraseViewModel
import com.dgsd.android.solar.onboarding.OnboardingCoordinator
import com.dgsd.android.solar.onboarding.createaccount.CreateAccountCoordinator
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object OnboardingViewModelModule {

    fun create(): Module {
        return module {
            viewModel<OnboardingCoordinator>()

            viewModel<CreateAccountCoordinator>()
            viewModel<CreateAccountViewSeedPhraseViewModel>()
        }
    }
}