package com.dgsd.android.solar.di

import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import coil.ImageLoader
import coil.decode.SvgDecoder
import com.dgsd.android.solar.BuildConfig
import com.dgsd.android.solar.applock.biometrics.AppLockBiometricManager
import com.dgsd.android.solar.applock.biometrics.AppLockBiometricManagerImpl
import com.dgsd.android.solar.applock.manager.AppLockManager
import com.dgsd.android.solar.applock.manager.AppLockManagerImpl
import com.dgsd.android.solar.cluster.manager.ClusterManager
import com.dgsd.android.solar.cluster.manager.ClusterManagerImpl
import com.dgsd.android.solar.common.clipboard.SystemClipboard
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.common.ui.PublicKeyFormatter
import com.dgsd.android.solar.files.FileProviderManager
import com.dgsd.android.solar.nfc.NfcManager
import com.dgsd.android.solar.nfc.NfcManagerImpl
import com.dgsd.android.solar.permission.PermissionsManager
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.android.solar.session.manager.SessionManagerImpl
import com.dgsd.ksol.model.Cluster
import okhttp3.OkHttpClient
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module
import java.time.Clock

private const val SHARED_PREFS_KEY_ACTIVE_SESSION = "session_manager_active_wallet"
private const val SHARED_PREFS_KEY_WALLET_SECRETS = "wallet_secrets"
private const val SHARED_PREFS_KEY_APP_SETTINGS = "app_settings"

internal object AppModule {

  fun create(): Module {
    return module {

      single(named(SHARED_PREFS_KEY_ACTIVE_SESSION)) {
        createSharedPreferences(SHARED_PREFS_KEY_ACTIVE_SESSION, MasterKeys.AES256_GCM_SPEC)
      }

      single(named(SHARED_PREFS_KEY_APP_SETTINGS)) {
        createSharedPreferences(SHARED_PREFS_KEY_APP_SETTINGS, MasterKeys.AES256_GCM_SPEC)
      }

      single(named(SHARED_PREFS_KEY_WALLET_SECRETS)) {
        val biometricManager = get<AppLockBiometricManager>()
        val keySpec = if (biometricManager.isAvailableOnDevice()) {
          biometricManager.createKeySpec()
        } else {
          MasterKeys.AES256_GCM_SPEC
        }

        createSharedPreferences(SHARED_PREFS_KEY_WALLET_SECRETS, keySpec)
      }

      single<SessionManager> {
        SessionManagerImpl(
          activeSessionSharedPreferences = get(named(SHARED_PREFS_KEY_ACTIVE_SESSION)),
          secretsSharedPreferences = lazy { get(named(SHARED_PREFS_KEY_WALLET_SECRETS)) }
        )
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

      single<AppLockManager> {
        AppLockManagerImpl(
          get(named(SHARED_PREFS_KEY_APP_SETTINGS)),
          clock = Clock.systemUTC()
        )
      }

      single<AppLockBiometricManager> {
        AppLockBiometricManagerImpl(get())
      }

      single<NfcManager> {
        NfcManagerImpl(get())
      }

      single<ImageLoader> {
        ImageLoader.Builder(get())
          .crossfade(true)
          .okHttpClient(get<OkHttpClient>())
          .components { add(SvgDecoder.Factory()) }
          .build()
      }

      singleOf(::PublicKeyFormatter)
      singleOf(::PermissionsManager)
      singleOf(::OkHttpClient)
      singleOf(::ErrorMessageFactory)
      singleOf(::SystemClipboard)
      singleOf(::FileProviderManager)
    }
  }

  private fun Scope.createSharedPreferences(
    fileName: String,
    keyGenParameterSpec: KeyGenParameterSpec,
  ): SharedPreferences {
    return EncryptedSharedPreferences.create(
      fileName,
      MasterKeys.getOrCreate(keyGenParameterSpec),
      get(),
      EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
      EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
  }
}