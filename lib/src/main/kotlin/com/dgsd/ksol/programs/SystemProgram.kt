package com.dgsd.ksol.programs

import com.dgsd.ksol.model.Lamports
import com.dgsd.ksol.model.PublicKey
import com.dgsd.ksol.model.TransactionInstruction
import org.bitcoinj.core.Utils

/**
 * @see <a href="https://docs.solana.com/developing/runtime-facilities/programs#system-program#>System Instruction Enum</a>
 */
object SystemProgram {
    val PROGRAM_ID = PublicKey.fromBase58("11111111111111111111111111111111")

    /**
     * The index of the different instructions available in the SystemProgram
     *
     * @see <a href="https://docs.rs/solana-sdk/1.8.2/solana_sdk/system_instruction/enum.SystemInstruction.html#>System Instruction Enum</a>
     */
    private const val PROGRAM_INDEX_TRANSFER = 2

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
            Utils.uint32ToByteArrayLE(PROGRAM_INDEX_TRANSFER.toLong(), this, 0)
            Utils.int64ToByteArrayLE(lamports, this, spaceForInstructionIndex)
        }

        return TransactionInstruction(
            programAccount = PROGRAM_ID,
            inputAccounts = listOf(sender, recipient),
            inputData = inputData
        )
    }
}