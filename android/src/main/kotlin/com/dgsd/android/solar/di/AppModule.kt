package com.dgsd.android.solar.di

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.android.solar.session.manager.SessionManagerImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val SHARED_PREFS_KEY_ACTIVE_SESSION = "session_manager_active_wallet"

internal object AppModule {

    fun create(): Module {
        return module {

            single(named(SHARED_PREFS_KEY_ACTIVE_SESSION)) {
                EncryptedSharedPreferences.create(
                    SHARED_PREFS_KEY_ACTIVE_SESSION,
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    get(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )

            }

            single<SessionManager> {
                SessionManagerImpl(get(named(SHARED_PREFS_KEY_ACTIVE_SESSION)))
            }

            singleOf(::ErrorMessageFactory)
        }
    }
}