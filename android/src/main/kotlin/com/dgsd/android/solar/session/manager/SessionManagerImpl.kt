package com.dgsd.android.solar.session.manager

import android.content.SharedPreferences
import androidx.core.content.edit
import com.dgsd.android.solar.common.model.SensitiveList
import com.dgsd.android.solar.common.model.SensitiveString
import com.dgsd.android.solar.model.AccountSeedInfo
import com.dgsd.android.solar.session.model.*
import com.dgsd.ksol.model.KeyPair
import com.dgsd.ksol.model.PrivateKey
import com.dgsd.ksol.model.PublicKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val PREF_KEY_ACTIVE_WALLET_PUBLIC_KEY_HASH = "active_wallet_public_key"

private const val PREF_KEY_SECRET_PASSPHRASE = "pass_phrase"
private const val PREF_KEY_SECRET_SEED_PHRASE = "seed_phrase"
private const val PREF_KEY_SECRET_ACTIVE_WALLET_PRIVATE_KEY = "active_wallet_private_key"

class SessionManagerImpl(
  private val activeSessionSharedPreferences: SharedPreferences,
  private val secretsSharedPreferences: Lazy<SharedPreferences>,
) : SessionManager {

  private val _activeSession = MutableStateFlow<Session>(NoActiveWalletSession)

  override val activeSession = _activeSession.asStateFlow()

  init {
    val publicKeyHash =
      activeSessionSharedPreferences.getString(PREF_KEY_ACTIVE_WALLET_PUBLIC_KEY_HASH, null)
    if (publicKeyHash != null) {
      runCatching {
        _activeSession.value = LockedAppSession(PublicKey.fromBase58(publicKeyHash))
      }
    }
  }

  override fun clear() {
    activeSessionSharedPreferences.edit { clear() }
    _activeSession.value = NoActiveWalletSession
  }

  override fun lockSession() {
    val current = _activeSession.value
    if (current is WalletSession) {
      _activeSession.value = LockedAppSession(current.publicKey)
    }
  }

  override fun upgradeSession() {
    when (val currentSession = _activeSession.value) {
      is LockedAppSession,
      NoActiveWalletSession -> {
        // We don't only allow upgrading when the user is logged in & unlocked
      }

      is KeyPairSession -> {
        // Nothing to do
      }

      is PublicKeySession -> {
        // We wrap this operation in the case that the user has not unlocked their keystore
        // (which is presumed to have happened when this method is called)
        runCatching {
          val privateKey = secretsSharedPreferences.value.getString(
            PREF_KEY_SECRET_ACTIVE_WALLET_PRIVATE_KEY,
            null
          )
          val passPhrase = secretsSharedPreferences.value.getString(
            PREF_KEY_SECRET_PASSPHRASE,
            null
          ).orEmpty()
          val seedPhrase = secretsSharedPreferences.value.getString(
            PREF_KEY_SECRET_SEED_PHRASE,
            ""
          )?.split(",")

          check(privateKey != null) { "Could not get private key" }
          check(seedPhrase != null) { "Could not get seed phrase" }

          setActiveSession(
            seedInfo = AccountSeedInfo(
              seedPhrase = SensitiveList(seedPhrase),
              passPhrase = SensitiveString(passPhrase)
            ),
            keyPair = KeyPair(
              publicKey = currentSession.publicKey,
              privateKey = PrivateKey.fromBase58(privateKey)
            )
          )
        }
      }
    }
  }

  override fun setActiveSession(publicKey: PublicKey) {
    _activeSession.value = PublicKeySession(publicKey)
    persistPublicKey(publicKey)
  }

  override fun setActiveSession(
    seedInfo: AccountSeedInfo,
    keyPair: KeyPair
  ) {
    persistSecretInfo(seedInfo, keyPair.privateKey)
    persistPublicKey(keyPair.publicKey)

    _activeSession.value = KeyPairSession(seedInfo, keyPair)
  }

  private fun persistPublicKey(publicKey: PublicKey) {
    activeSessionSharedPreferences.edit {
      putString(PREF_KEY_ACTIVE_WALLET_PUBLIC_KEY_HASH, publicKey.toBase58String())
    }
  }

  private fun persistSecretInfo(seedInfo: AccountSeedInfo, privateKey: PrivateKey) {
    secretsSharedPreferences.value.edit {
      putString(PREF_KEY_SECRET_ACTIVE_WALLET_PRIVATE_KEY, privateKey.toBase58String())
      putString(PREF_KEY_SECRET_PASSPHRASE, seedInfo.passPhrase.sensitiveValue)
      putString(PREF_KEY_SECRET_SEED_PHRASE, seedInfo.seedPhrase.joinToString(","))
    }
  }
}