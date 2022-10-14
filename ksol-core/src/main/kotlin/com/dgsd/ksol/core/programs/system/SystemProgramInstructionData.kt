package com.dgsd.ksol.core.programs.system

import com.dgsd.ksol.core.model.Lamports

class SystemProgramInstructionData(
  val instruction: SystemProgramInstruction,
  val lamports: Lamports
)