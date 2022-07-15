package com.dgsd.android.solar.di

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.dgsd.android.solar.BuildConfig
import com.dgsd.android.solar.cluster.manager.ClusterManager
import com.dgsd.android.solar.cluster.manager.ClusterManagerImpl
import com.dgsd.android.solar.common.clipboard.SystemClipboard
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.android.solar.session.manager.SessionManagerImpl
import com.dgsd.ksol.model.Cluster
import okhttp3.OkHttpClient
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

private const val SHARED_PREFS_KEY_ACTIVE_SESSION = "session_manager_active_wallet"
private const val SHARED_PREFS_KEY_APP_SETTINGS = "app_settings"

internal object AppModule {

    fun create(): Module {
        return module {

            single(named(SHARED_PREFS_KEY_ACTIVE_SESSION)) {
                createSharedPreferences(SHARED_PREFS_KEY_ACTIVE_SESSION)
            }

            single(named(SHARED_PREFS_KEY_APP_SETTINGS)) {
                createSharedPreferences(SHARED_PREFS_KEY_APP_SETTINGS)
            }

            single<SessionManager> {
                SessionManagerImpl(get(named(SHARED_PREFS_KEY_ACTIVE_SESSION)))
            }

            single<ClusterManager> {
                ClusterManagerImpl(
                    get(named(SHARED_PREFS_KEY_APP_SETTINGS)),
                    if (BuildConfig.DEBUG) {
                        Cluster.DEVNET
                    } else {
                        Cluster.MAINNET
                    }
                )
            }

            singleOf(::OkHttpClient)
            singleOf(::ErrorMessageFactory)
            singleOf(::SystemClipboard)
        }
    }

    private fun Scope.createSharedPreferences(fileName: String): SharedPreferences {
        return EncryptedSharedPreferences.create(
            fileName,
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            get(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}