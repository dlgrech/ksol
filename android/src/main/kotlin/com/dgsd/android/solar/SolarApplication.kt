package com.dgsd.android.solar

import android.app.Application
import android.os.StrictMode
import com.dgsd.android.solar.di.AppModule
import com.dgsd.android.solar.di.SessionScopedModule
import com.dgsd.android.solar.di.ViewModelModule
import com.google.android.material.color.DynamicColors
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SolarApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)

        startKoin {
            if (BuildConfig.DEBUG) {
                androidLogger()
            }

            androidContext(this@SolarApplication)

            modules(
                AppModule.create(),
                SessionScopedModule.create(),
                ViewModelModule.create(),
            )
        }

        if (BuildConfig.DEBUG) {
            StrictMode.enableDefaults()
        }
    }
}