package com.dgsd.ksol.core

import com.dgsd.ksol.core.model.*
import com.dgsd.ksol.core.programs.system.SystemProgram
import com.dgsd.ksol.core.serialization.LocalTransactionSerialization
import com.dgsd.ksol.core.utils.EncodingUtils
import com.dgsd.ksol.core.utils.SigningUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LocalTransactionsTest {

  @Test
  fun isValidSignature_whenIndexOutOfBounds_throwsException() {
    val keyPairs = listOf(
      KeyPair(
        publicKey = PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"),
        privateKey = PrivateKey.fromBase58(
          "5ysPKzei6U5b1KTRs7XjwUL7335E8L1eta531oQkXP63Wf1jkavEyov1zyNX928hHhNkfEpVptACSfWPZtbzgeoa"
        )
      )
    )
    val message = createTransactionMessage(keyPairs)
    val signatures = keyPairs.map {
      EncodingUtils.encodeBase58(
        SigningUtils.sign(LocalTransactionSerialization.serialize(message), it.privateKey)
      )
    }

    val transaction = LocalTransaction(signatures, message)

    Assertions.assertThrows(IndexOutOfBoundsException::class.java) {
      LocalTransactions.isValidSignature(transaction, 2)
    }
  }

  @Test
  fun isValidSignature_withAllInvalid_returnsFalse() {
    val keyPairs = listOf(
      KeyPair(
        publicKey = PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"),
        privateKey = PrivateKey.fromBase58(
          "5ysPKzei6U5b1KTRs7XjwUL7335E8L1eta531oQkXP63Wf1jkavEyov1zyNX928hHhNkfEpVptACSfWPZtbzgeoa"
        )
      )
    )
    val message = createTransactionMessage(keyPairs)
    val signatures = keyPairs.map { "111111111" }

    val transaction = LocalTransaction(signatures, message)

    Assertions.assertFalse(LocalTransactions.isValidSignature(transaction, 0))
  }

  @Test
  fun isValidSignature_withSingleInvalid_returnsFalse() {
    val keyPairs = listOf(
      KeyPair(
        publicKey = PublicKey.fromBase58("CL2s8d6T8tULpX6vLRHLdhF5omU9SKj3N5PGpygZYjh7"),
        privateKey = PrivateKey.fromBase58("HhvXvokRemjtCgC8qSAAgfZ8BHXvsSwwCUuWKeDeLtAQ61HHVhdL32bPFRJw4ZCSCpTTFtwKkiKNwfgafkzneER")
      ),

      KeyPair(
        publicKey = PublicKey.fromBase58("DB7tgWejnGXydW9tvyr9atsBhmB6CDz326KG2nnm96HN"),
        privateKey = PrivateKey.fromBase58("4s69nsCdwTKWFWrrriLkc3cptfH8niYh7axq3r5odKK9rayd8A64Zie2JfVcg8QHAm7XHN5WwdzHYEnZKxWQcxyN")
      ),
    )
    val message = createTransactionMessage(keyPairs)
    val signatures = keyPairs.mapIndexed { index, keyPair ->
      if (index == 0) {
        EncodingUtils.encodeBase58(
          SigningUtils.sign(LocalTransactionSerialization.serialize(message), keyPair.privateKey)
        )
      } else {
        "111111111"
      }
    }

    val transaction = LocalTransaction(signatures, message)

    Assertions.assertTrue(LocalTransactions.isValidSignature(transaction, 0))
    Assertions.assertFalse(LocalTransactions.isValidSignature(transaction, 1))
  }

  @Test
  fun isValidSignature_withSingleValid_returnsTrue() {
    val keyPairs = listOf(
      KeyPair(
        publicKey = PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"),
        privateKey = PrivateKey.fromBase58(
          "5ysPKzei6U5b1KTRs7XjwUL7335E8L1eta531oQkXP63Wf1jkavEyov1zyNX928hHhNkfEpVptACSfWPZtbzgeoa"
        )
      )
    )
    val message = createTransactionMessage(keyPairs)
    val signatures = keyPairs.map {
      EncodingUtils.encodeBase58(
        SigningUtils.sign(LocalTransactionSerialization.serialize(message), it.privateKey)
      )
    }

    val transaction = LocalTransaction(signatures, message)

    Assertions.assertTrue(LocalTransactions.isValidSignature(transaction, 0))
  }

  @Test
  fun isValidSignature_withAllValid_returnsTrue() {
    val keyPairs = listOf(
      KeyPair(
        publicKey = PublicKey.fromBase58("CL2s8d6T8tULpX6vLRHLdhF5omU9SKj3N5PGpygZYjh7"),
        privateKey = PrivateKey.fromBase58("HhvXvokRemjtCgC8qSAAgfZ8BHXvsSwwCUuWKeDeLtAQ61HHVhdL32bPFRJw4ZCSCpTTFtwKkiKNwfgafkzneER")
      ),

      KeyPair(
        publicKey = PublicKey.fromBase58("DB7tgWejnGXydW9tvyr9atsBhmB6CDz326KG2nnm96HN"),
        privateKey = PrivateKey.fromBase58("4s69nsCdwTKWFWrrriLkc3cptfH8niYh7axq3r5odKK9rayd8A64Zie2JfVcg8QHAm7XHN5WwdzHYEnZKxWQcxyN")
      ),
    )
    val message = createTransactionMessage(keyPairs)
    val signatures = keyPairs.map {
      EncodingUtils.encodeBase58(
        SigningUtils.sign(LocalTransactionSerialization.serialize(message), it.privateKey)
      )
    }

    val transaction = LocalTransaction(signatures, message)

    Assertions.assertTrue(LocalTransactions.isValidSignature(transaction, 0))
    Assertions.assertTrue(LocalTransactions.isValidSignature(transaction, 1))
  }

  private fun createTransactionMessage(keyPairs: List<KeyPair>): TransactionMessage {
    val accounts = keyPairs.mapIndexed { index, key ->
      TransactionAccountMetadata(
        publicKey = key.publicKey,
        isSigner = index == 0,
        isFeePayer = index == 0,
        isWritable = index <= 1
      )
    }

    return TransactionMessage(
      header = TransactionHeader.createFrom(accounts),
      accountKeys = accounts,
      recentBlockhash = PublicKey.fromBase58("9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ"),
      instructions = listOf(
        TransactionInstruction(
          programAccount = SystemProgram.PROGRAM_ID,
          inputData = byteArrayOf(1, 2, 3),
          inputAccounts = accounts.dropLast(1).map { it.publicKey }
        )
      )
    )
  }
}