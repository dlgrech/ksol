package com.dgsd.android.solar.session.manager

import android.content.SharedPreferences
import androidx.core.content.edit
import com.dgsd.android.solar.session.model.KeyPairSession
import com.dgsd.android.solar.session.model.NoActiveWalletSession
import com.dgsd.android.solar.session.model.PublicKeySession
import com.dgsd.android.solar.session.model.Session
import com.dgsd.ksol.model.KeyPair
import com.dgsd.ksol.model.PublicKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val PREF_KEY_ACTIVE_WALLET_PUBLIC_KEY_HASH = "active_wallet_public_key"

class SessionManagerImpl(
    private val sharedPreferences: SharedPreferences,
) : SessionManager {

    private val _activeSession = MutableStateFlow<Session>(NoActiveWalletSession)

    override val activeSession = _activeSession.asStateFlow()

    init {
        val publicKeyHash =
            sharedPreferences.getString(PREF_KEY_ACTIVE_WALLET_PUBLIC_KEY_HASH, null)
        if (publicKeyHash != null) {
            runCatching {
                setActiveSession(PublicKey.fromBase58(publicKeyHash))
            }
        }
    }

    override fun clear() {
        _activeSession.value = NoActiveWalletSession
    }

    override fun setActiveSession(publicKey: PublicKey) {
        _activeSession.value = PublicKeySession(publicKey)
        persistPublicKey(publicKey)
    }

    override fun setActiveSession(keyPair: KeyPair) {
        _activeSession.value = KeyPairSession(keyPair)
        persistPublicKey(keyPair.publicKey)
    }

    private fun persistPublicKey(publicKey: PublicKey) {
        sharedPreferences.edit {
            putString(PREF_KEY_ACTIVE_WALLET_PUBLIC_KEY_HASH, publicKey.toBase58String())
        }
    }
}