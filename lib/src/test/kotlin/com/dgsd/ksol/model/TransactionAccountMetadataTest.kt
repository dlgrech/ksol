package com.dgsd.ksol.model

import com.dgsd.ksol.programs.system.SystemProgram
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TransactionAccountMetadataTest {

    @Test
    fun indexOf_whenNotContainedInList_returnsNegative1() {
        val first = TransactionAccountMetadata(
            PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"),
            isSigner = true,
            isFeePayer = true,
            isWritable = true
        )

        val second = TransactionAccountMetadata(
            SystemProgram.PROGRAM_ID,
            isSigner = false,
            isFeePayer = false,
            isWritable = false
        )

        Assertions.assertEquals(
            -1,
            TransactionAccountMetadata.indexOf(listOf(first), second.publicKey)
        )
    }

    @Test
    fun indexOf_whenMultipleMatchingEntriesInList_returnsFirst() {
        val first = TransactionAccountMetadata(
            SystemProgram.PROGRAM_ID,
            isSigner = true,
            isFeePayer = true,
            isWritable = true
        )

        val second = TransactionAccountMetadata(
            SystemProgram.PROGRAM_ID,
            isSigner = false,
            isFeePayer = false,
            isWritable = false
        )

        Assertions.assertEquals(
            0,
            TransactionAccountMetadata.indexOf(listOf(first, second), second.publicKey)
        )
    }

    @Test
    fun indexOf_whenContainedInList_returnsIndex() {
        val first = TransactionAccountMetadata(
            PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"),
            isSigner = true,
            isFeePayer = true,
            isWritable = true
        )

        val second = TransactionAccountMetadata(
            SystemProgram.PROGRAM_ID,
            isSigner = false,
            isFeePayer = false,
            isWritable = false
        )

        Assertions.assertEquals(
            1,
            TransactionAccountMetadata.indexOf(listOf(first, second), second.publicKey)
        )
    }

    @Test
    fun collapse_whenContainingSameAddressWithDifferentProperties_updatesAccount() {
        val first = TransactionAccountMetadata(
            PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"),
            isSigner = true,
            isFeePayer = true,
            isWritable = true
        )

        val second = TransactionAccountMetadata(
            SystemProgram.PROGRAM_ID,
            isSigner = false,
            isFeePayer = false,
            isWritable = false
        )

        val third = TransactionAccountMetadata(
            SystemProgram.PROGRAM_ID,
            isSigner = true,
            isFeePayer = false,
            isWritable = false
        )

        val fourth = TransactionAccountMetadata(
            SystemProgram.PROGRAM_ID,
            isSigner = true,
            isFeePayer = true,
            isWritable = true
        )

        val input = listOf(first, second, third, fourth)

        Assertions.assertEquals(
            listOf(first, fourth),
            TransactionAccountMetadata.collapse(input)
        )
    }

    @Test
    fun collapse_wheNoDuplicatesAndSorted_returnsSameList() {
        val first = TransactionAccountMetadata(
            PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"),
            isSigner = true,
            isFeePayer = true,
            isWritable = true
        )

        val second = TransactionAccountMetadata(
            SystemProgram.PROGRAM_ID,
            isSigner = false,
            isFeePayer = false,
            isWritable = false
        )

        val input = listOf(first, second)

        Assertions.assertEquals(
            listOf(first, second),
            TransactionAccountMetadata.collapse(input)
        )
    }

    @Test
    fun collapse_wheNoDuplicatesAndUnsorted_returnsSortedList() {
        val first = TransactionAccountMetadata(
            PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"),
            isSigner = true,
            isFeePayer = true,
            isWritable = true
        )

        val second = TransactionAccountMetadata(
            PublicKey.fromBase58("4ETf86tK7b4W72f27kNLJLgRWi9UfJjgH4koHGUXMFtn"),
            isSigner = false,
            isFeePayer = false,
            isWritable = true
        )

        val third = TransactionAccountMetadata(
            SystemProgram.PROGRAM_ID,
            isSigner = false,
            isFeePayer = false,
            isWritable = false
        )

        val input = listOf(third, second, first)

        Assertions.assertEquals(
            listOf(first, second, third),
            TransactionAccountMetadata.collapse(input)
        )
    }
}