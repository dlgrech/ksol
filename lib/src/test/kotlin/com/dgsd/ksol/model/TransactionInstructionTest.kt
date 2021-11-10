package com.dgsd.ksol.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TransactionInstructionTest {

    @Test
    fun equals_forSameInstance_returnsTrue() {
        val instruction = TransactionInstruction(
            programAccount = PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"),
            inputData = byteArrayOf(1, 2, 3),
            inputAccounts = listOf(PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"))
        )

        Assertions.assertEquals(instruction, instruction)
    }

    @Test
    fun equals_forDifferentInstanceWithSameContent_returnsTrue() {
        val first = TransactionInstruction(
            programAccount = PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"),
            inputData = byteArrayOf(1, 2, 3),
            inputAccounts = listOf(PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"))
        )

        val second = TransactionInstruction(
            programAccount = PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"),
            inputData = byteArrayOf(1, 2, 3),
            inputAccounts = listOf(PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"))
        )

        Assertions.assertEquals(first, second)
    }

    @Test
    fun equals_forDifferentType_returnsFalse() {
        val first = TransactionInstruction(
            programAccount = PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"),
            inputData = byteArrayOf(1, 2, 3),
            inputAccounts = listOf(PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"))
        )

        Assertions.assertNotEquals(first, "this is not a public key")
    }

    @Test
    fun equals_forNull_returnsFalse() {
        val first = TransactionInstruction(
            programAccount = PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"),
            inputData = byteArrayOf(1, 2, 3),
            inputAccounts = listOf(PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"))
        )

        Assertions.assertNotEquals(first, null)
    }

    @Test
    fun equals_withDifferentContent_returnsFalse() {
        val first = TransactionInstruction(
            programAccount = PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"),
            inputData = byteArrayOf(1, 2, 3),
            inputAccounts = listOf(PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"))
        )

        val second = TransactionInstruction(
            programAccount = PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"),
            inputData = byteArrayOf(4, 5, 6),
            inputAccounts = listOf(PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"))
        )

        Assertions.assertNotEquals(first, second)
    }
}