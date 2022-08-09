package com.dgsd.android.solar.extensions

import com.dgsd.ksol.model.PublicKey
import com.dgsd.ksol.model.TransactionMessage
import com.dgsd.ksol.programs.memo.MemoProgram
import com.dgsd.ksol.programs.system.SystemProgram
import com.dgsd.ksol.programs.system.SystemProgramInstructionData

fun TransactionMessage.getSystemProgramInstruction(): SystemProgramInstructionData? {
  return runCatching {
    val instruction =
      instructions.firstOrNull { it.programAccount == SystemProgram.PROGRAM_ID }
    if (instruction == null) {
      null
    } else {
      SystemProgram.decodeInstruction(instruction.inputData)
    }
  }.getOrNull()
}

fun TransactionMessage.getMemoMessage(): String? {
  return runCatching {
    val memoInstruction = instructions.firstOrNull { it.programAccount == MemoProgram.PROGRAM_ID }
    if (memoInstruction == null) {
      null
    } else {
      MemoProgram.decodeInstruction(memoInstruction.inputData)
    }
  }.getOrNull()
}

fun TransactionMessage.extractBestDisplayRecipient(currentSession: PublicKey): PublicKey? {
  return accountKeys
    .firstOrNull { account -> account.isWritable && account.publicKey != currentSession }
    ?.publicKey
}