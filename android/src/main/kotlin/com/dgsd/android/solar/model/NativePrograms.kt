package com.dgsd.android.solar.model

import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.programs.*
import com.dgsd.ksol.core.programs.memo.MemoProgram
import com.dgsd.ksol.core.programs.system.SystemProgram

object NativePrograms {

  private val nativeProgramKeys = setOf(
    BPFLoaderProgram.PROGRAM_ID,
    ConfigProgram.PROGRAM_ID,
    Ed25519Program.PROGRAM_ID,
    Secp256k1Program.PROGRAM_ID,
    StakeProgram.PROGRAM_ID,
    SystemProgram.PROGRAM_ID,
    VoteProgram.PROGRAM_ID,
    MemoProgram.PROGRAM_ID,
  )

  fun isNativeProgram(publicKey: PublicKey): Boolean {
    return publicKey in nativeProgramKeys
  }
}