package com.dgsd.ksol.core.model

import com.dgsd.ksol.core.programs.system.SystemProgram
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TransactionMessageTest {

    @Test
    fun constructor_whenNoAccountKeysPassed_throwsException() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            TransactionMessage(
                header = TransactionHeader(1, 1, 1),
                accountKeys = emptyList(),
                recentBlockhash = PublicKey.fromBase58("9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ"),
                instructions = listOf(
                    TransactionInstruction(
                        programAccount = SystemProgram.PROGRAM_ID,
                        inputData = byteArrayOf(1, 2, 3),
                        inputAccounts = emptyList()
                    )
                )
            )
        }
    }

    @Test
    fun constructor_whenNoInstructionsPassed_throwsException() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            TransactionMessage(
                header = TransactionHeader(1, 1, 1),
                accountKeys = listOf(
                    TransactionAccountMetadata(
                        PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"),
                        isSigner = true,
                        isFeePayer = true,
                        isWritable = true
                    )
                ),
                recentBlockhash = PublicKey.fromBase58("9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ"),
                instructions = emptyList()
            )
        }
    }
}