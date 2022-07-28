package com.dgsd.android.solar.common.ui

import android.content.Context
import com.dgsd.android.solar.R
import com.dgsd.ksol.model.PublicKey
import com.dgsd.ksol.programs.*
import com.dgsd.ksol.programs.system.SystemProgram

class PublicKeyFormatter(
  private val context: Context
) {

  fun format(publicKey: PublicKey): CharSequence {
    val friendlyName = when (publicKey) {
      BPFLoaderProgram.PROGRAM_ID -> context.getString(R.string.key_display_bpf_loader_program)
      ConfigProgram.PROGRAM_ID -> context.getString(R.string.key_display_config_program)
      Ed25519Program.PROGRAM_ID -> context.getString(R.string.key_display_ed25519_program)
      Secp256k1Program.PROGRAM_ID -> context.getString(R.string.key_display_secp256k1_program)
      StakeProgram.PROGRAM_ID -> context.getString(R.string.key_display_stake_program)
      SystemProgram.PROGRAM_ID -> context.getString(R.string.key_display_system_program)
      VoteProgram.PROGRAM_ID -> context.getString(R.string.key_display_vote_program)
      TESTNET_FAUCET -> context.getString(R.string.key_display_testnet_faucet)
      DEVNET_FAUCET -> context.getString(R.string.key_display_devnet_faucet)
      else -> null
    }

    return if (friendlyName == null) {
      publicKey.toBase58String()
    } else {
      "$friendlyName (${abbreviate(publicKey)})"
    }
  }

  fun abbreviate(publicKey: PublicKey): CharSequence {
    val base58 = publicKey.toBase58String()
    val prefix = base58.take(5)
    val suffix = base58.takeLast(5)

    return "$prefix…$suffix"
  }

  companion object {
    private val TESTNET_FAUCET = PublicKey.fromBase58("4ETf86tK7b4W72f27kNLJLgRWi9UfJjgH4koHGUXMFtn")
    private val DEVNET_FAUCET = PublicKey.fromBase58("9B5XszUGdMaxCZ7uSQhPzdks5ZQSmWxrmzCSvtJ6Ns6g")
  }
}
