package com.dgsd.android.solar.onboarding.di

import com.dgsd.android.solar.onboarding.OnboardingCoordinator
import com.dgsd.android.solar.onboarding.createaccount.CreateAccountAddressSelectionViewModel
import com.dgsd.android.solar.onboarding.createaccount.CreateAccountCoordinator
import com.dgsd.android.solar.onboarding.createaccount.CreateAccountEnterPassphraseViewModel
import com.dgsd.android.solar.onboarding.createaccount.CreateAccountViewSeedPhraseViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module

object OnboardingViewModelModule {

    fun create(): Module {
        return module {
            viewModelOf(::OnboardingCoordinator)

            viewModelOf(::CreateAccountCoordinator)
            viewModelOf(::CreateAccountEnterPassphraseViewModel)
            viewModelOf(::CreateAccountViewSeedPhraseViewModel)
            viewModelOf(::CreateAccountAddressSelectionViewModel)
        }
    }
}