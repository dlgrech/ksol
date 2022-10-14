package com.dgsd.android.solar.session.model

import com.dgsd.android.solar.model.AccountSeedInfo
import com.dgsd.ksol.core.model.KeyPair
import com.dgsd.ksol.core.model.PrivateKey
import com.dgsd.ksol.core.model.PublicKey

/**
 * Represents the authentication-level of the wallet being interacted with by the app
 */
sealed interface Session {

  /**
   * Used to uniquely identify the same wallet
   */
  val sessionId: String
}

/**
 * [Session] when there is no wallet currently active
 */
object NoActiveWalletSession : Session {

  override val sessionId = "no_active_wallet_session"
}

/**
 * [Session] instance tied to a particular account, but the app will need to verify the user first
 */
data class LockedAppSession(val publicKey: PublicKey) : Session {

  override val sessionId: String
    get() = "locked_${publicKey.toBase58String()}"
}

/**
 * A [Session] that is tied to a particular account, as identified by [publicKey]
 */
sealed class WalletSession : Session {

  /**
   * The [PublicKey] associated with the wallet being interacted with by the app
   */
  abstract val publicKey: PublicKey

  override val sessionId: String
    get() = publicKey.toBase58String()
}

/**
 * A [WalletSession] based on only a [PublicKey]. This is the [Session] used when a user has not
 * given access to the underlying [PrivateKey] for the wallet
 */
data class PublicKeySession(override val publicKey: PublicKey) : WalletSession()

/**
 * A [WalletSession] with both a [PublicKey] and a [PrivateKey]. This is the [Session] used when a
 * user has given access to the [PrivateKey] of the underlying wallet
 */
data class KeyPairSession(
  val seedInfo: AccountSeedInfo,
  val keyPair: KeyPair,
) : WalletSession() {

  override val publicKey = keyPair.publicKey
}