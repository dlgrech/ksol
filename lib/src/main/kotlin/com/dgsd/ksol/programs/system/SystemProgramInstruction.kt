package com.dgsd.ksol.programs.system

/**
 * The index of the different instructions available in the SystemProgram
 *
 * @see <a href="https://docs.rs/solana-sdk/1.8.2/solana_sdk/system_instruction/enum.SystemInstruction.html#>System Instruction Enum</a>
 */
enum class SystemProgramInstruction {

  CREATE_ACCOUNT,
  ASSIGN,
  TRANSFER,
  CREATE_ACCOUNT_WITH_SEED,
  ADVANCE_NONCE_ACCOUNT,
  WITHDRAW_NONCE_ACCOUNT,
  INITIALIZE_NONCE_ACCOUNT,
  AUTHORIZE_NONCE_ACCOUNT,
  ALLOCATE,
  ALLOCATE_WITH_SEED,
  ASSIGN_WITH_SEED,
  TRANSFER_WITH_SEED,
}