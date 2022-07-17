package com.dgsd.android.solar.onboarding.di

import com.dgsd.android.solar.di.util.getScoped
import com.dgsd.android.solar.onboarding.OnboardingCoordinator
import com.dgsd.android.solar.onboarding.createaccount.CreateAccountConfirmationViewModel
import com.dgsd.android.solar.onboarding.createaccount.CreateAccountCoordinator
import com.dgsd.android.solar.onboarding.createaccount.CreateAccountEnterPassphraseViewModel
import com.dgsd.android.solar.onboarding.createaccount.CreateAccountViewSeedPhraseViewModel
import com.dgsd.android.solar.onboarding.restoreaccount.RestoreAccountCoordinator
import com.dgsd.android.solar.onboarding.restoreaccount.RestoreAccountSelectAddressViewModel
import com.dgsd.android.solar.onboarding.restoreaccount.RestoreAccountViewSeedPhraseViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
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
            viewModelOf(::CreateAccountConfirmationViewModel)

            viewModelOf(::RestoreAccountCoordinator)
            viewModelOf(::RestoreAccountViewSeedPhraseViewModel)
            viewModel {
                RestoreAccountSelectAddressViewModel(
                    errorMessageFactory = get(),
                    solanaApi = getScoped(),
                    seedPhrase = get(),
                )
            }
        }
    }
}