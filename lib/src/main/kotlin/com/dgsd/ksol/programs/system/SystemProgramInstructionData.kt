package com.dgsd.ksol.programs.system

import com.dgsd.ksol.model.Lamports

class SystemProgramInstructionData(
  val instruction: SystemProgramInstruction,
  val lamports: Lamports
)