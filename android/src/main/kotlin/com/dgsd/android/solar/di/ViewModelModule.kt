package com.dgsd.android.solar.di

import com.dgsd.android.solar.AppCoordinator
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

internal object ViewModelModule {

    fun create(): Module {
        return module {
            viewModel<AppCoordinator>()
        }
    }
}