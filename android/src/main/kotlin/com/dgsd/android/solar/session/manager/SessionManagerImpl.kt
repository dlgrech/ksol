package com.dgsd.android.solar.session.manager

import android.content.SharedPreferences
import androidx.core.content.edit
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
    _activeSession.value = NoActiveWalletSession
  }

  override fun lockSession() {
    val current = _activeSession.value
    if (current is WalletSession) {
      _activeSession.value = LockedAppSession(current.publicKey)
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