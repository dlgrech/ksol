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
    return when (publicKey) {
      BPFLoaderProgram.PROGRAM_ID -> context.getString(R.string.key_display_bpf_loader_program)
      ConfigProgram.PROGRAM_ID -> context.getString(R.string.key_display_config_program)
      Ed25519Program.PROGRAM_ID -> context.getString(R.string.key_display_ed25519_program)
      Secp256k1Program.PROGRAM_ID -> context.getString(R.string.key_display_secp256k1_program)
      StakeProgram.PROGRAM_ID -> context.getString(R.string.key_display_stake_program)
      SystemProgram.PROGRAM_ID -> context.getString(R.string.key_display_system_program)
      VoteProgram.PROGRAM_ID -> context.getString(R.string.key_display_vote_program)
      else -> publicKey.toBase58String()
    }
  }
}
