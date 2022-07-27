package com.dgsd.ksol.model

import com.dgsd.ksol.programs.system.SystemProgram
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TransactionTest {

    private lateinit var message: TransactionMessage

    @BeforeEach
    fun setup() {
        val keys = listOf(
            TransactionAccountMetadata(
                PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"),
                isSigner = true,
                isWritable = true,
                isFeePayer = true,
            )
        )
        message = TransactionMessage(
            header = TransactionHeader.createFrom(keys),
            accountKeys = keys,
            recentBlockhash = PublicKey.fromBase58("9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ"),
            instructions = listOf(
                TransactionInstruction(
                    programAccount = SystemProgram.PROGRAM_ID,
                    inputData = byteArrayOf(1, 2, 3),
                    inputAccounts = keys.map { it.publicKey }
                )
            )
        )
    }

    @Test
    fun constructor_whenSignaturesIsEmpty_throwsException() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Transaction(
                slot = 1,
                blockTime = null,
                signatures = emptyList(),
                message = message,
                metadata = TransactionMetadata(1, emptyList(), emptyList()),
            )
        }
    }

    @Test
    fun id_withMultipleSignatures_returnsFirst() {
        val id = Transaction(
            slot = 1,
            blockTime = null,
            signatures = listOf("a", "b", "c"),
            message = message,
            metadata = TransactionMetadata(1, emptyList(), emptyList()),
        ).id

        Assertions.assertEquals("a", id)
    }

    @Test
    fun id_withSingleSignatures_returnsFirst() {
        val id = Transaction(
            slot = 1,
            blockTime = null,
            signatures = listOf("a"),
            message = message,
            metadata = TransactionMetadata(1, emptyList(), emptyList()),
        ).id

        Assertions.assertEquals("a", id)
    }
}