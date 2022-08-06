package com.dgsd.android.solar.extensions

import com.dgsd.ksol.model.TransactionMessage
import com.dgsd.ksol.programs.system.SystemProgram
import com.dgsd.ksol.programs.system.SystemProgramInstructionData

fun TransactionMessage.getSystemProgramInstruction(): SystemProgramInstructionData? {
  return runCatching {
    val singleInstruction = instructions.singleOrNull()
    if (singleInstruction?.programAccount != SystemProgram.PROGRAM_ID) {
      null
    } else {
      SystemProgram.decodeInstruction(singleInstruction.inputData)
    }
  }.getOrNull()
}