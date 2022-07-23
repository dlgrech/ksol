package com.dgsd.ksol.programs.system

import com.dgsd.ksol.model.Lamports
import com.dgsd.ksol.model.PublicKey
import com.dgsd.ksol.model.TransactionInstruction
import org.bitcoinj.core.Utils

/**
 * @see <a href="https://docs.solana.com/developing/runtime-facilities/programs#system-program#>System Instruction Enum</a>
 */
object SystemProgram {
    val PROGRAM_ID = PublicKey.fromBase58("11111111111111111111111111111111")

    fun transfer(
        sender: PublicKey,
        recipient: PublicKey,
        lamports: Lamports,
    ): TransactionInstruction {
        require(sender != recipient) {
            "Trying to send to same address"
        }

        require(lamports > 0) {
            "Lamports must be > 0"
        }

        val spaceForInstructionIndex = 4
        val spaceForLamports = 8

        val inputData = ByteArray(spaceForInstructionIndex + spaceForLamports).apply {
            Utils.uint32ToByteArrayLE(SystemProgramInstruction.TRANSFER.ordinal.toLong(), this, 0)
            Utils.int64ToByteArrayLE(lamports, this, spaceForInstructionIndex)
        }

        return TransactionInstruction(
            programAccount = PROGRAM_ID,
            inputAccounts = listOf(sender, recipient),
            inputData = inputData
        )
    }

    fun decodeInstruction(byteArray: ByteArray): SystemProgramInstructionData {
        return try {
            val programIndex = Utils.readUint32(byteArray, 0).toInt()
            val lamports = Utils.readInt64(byteArray, 4)

            SystemProgramInstructionData(
                SystemProgramInstruction.values()[programIndex],
                lamports
            )
        } catch (t: Throwable) {
            throw IllegalArgumentException("Invalid input data", t)
        }
    }
}