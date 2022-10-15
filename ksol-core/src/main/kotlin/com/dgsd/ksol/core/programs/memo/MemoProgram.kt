package com.dgsd.ksol.core.programs.memo

import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.model.TransactionInstruction

/**
 * @see <a href="https://spl.solana.com/memo>Memo Program</a>
 */
object MemoProgram {

  val PROGRAM_ID = PublicKey.fromBase58("MemoSq4gqABAXKb96qnH8TysNcWxMyWCqXgDLGmfcHr")

  fun decodeInstruction(byteArray: ByteArray): String {
    return byteArray.decodeToString()
  }

  fun createInstruction(memoText: String): TransactionInstruction {
    return TransactionInstruction(
      programAccount = PROGRAM_ID,
      inputAccounts = emptyList(),
      inputData = memoText.toByteArray()
    )
  }
}