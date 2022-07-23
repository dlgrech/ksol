package com.dgsd.ksol.programs.system

import com.dgsd.ksol.utils.DecodingUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SystemProgramTest {

  @Test
  fun decodeInstruction_withValidInput_returnsExpectedInstruction() {
    val base58Instruction = "3Bxs3zzLZLuLQEYX"
    val decodedInstruction = DecodingUtils.decodeBase58(base58Instruction)

    val instruction = SystemProgram.decodeInstruction(decodedInstruction)

    Assertions.assertEquals(SystemProgramInstruction.TRANSFER, instruction.instruction)
    Assertions.assertEquals(1000000000, instruction.lamports)
  }

  @Test
  fun decodeInstruction_withEmptyInput_returnsExpectedInstruction() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      SystemProgram.decodeInstruction(byteArrayOf())
    }
  }

  @Test
  fun decodeInstruction_withBaseInput_returnsExpectedInstruction() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      val base58Instruction = "3Bxs3zzLZLuLQEYX3Bxs3zzLZLuLQEYX"
      val decodedInstruction = DecodingUtils.decodeBase58(base58Instruction)

      SystemProgram.decodeInstruction(decodedInstruction)
    }
  }
}