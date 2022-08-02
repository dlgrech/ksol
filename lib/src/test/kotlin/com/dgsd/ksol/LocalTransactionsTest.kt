package com.dgsd.ksol

import com.dgsd.ksol.keygen.KeyFactory
import com.dgsd.ksol.keygen.MnemonicPhraseLength
import com.dgsd.ksol.model.*
import com.dgsd.ksol.programs.system.SystemProgram
import com.dgsd.ksol.serialization.LocalTransactionSerialization
import com.dgsd.ksol.utils.EncodingUtils
import com.dgsd.ksol.utils.SigningUtils
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LocalTransactionsTest {

  @Test
  fun isValidSignature_whenIndexOutOfBounds_throwsException() {
    val keyPairs = createAccountKeyPairs(1)
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
    val keyPairs = createAccountKeyPairs(1)
    val message = createTransactionMessage(keyPairs)
    val signatures = keyPairs.map { "111111111" }

    val transaction = LocalTransaction(signatures, message)

    Assertions.assertFalse(LocalTransactions.isValidSignature(transaction, 0))
  }

  @Test
  fun isValidSignature_withSingleInvalid_returnsFalse() {
    val keyPairs = createAccountKeyPairs(2)
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
    val keyPairs = createAccountKeyPairs(1)
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
    val keyPairs = createAccountKeyPairs(2)
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

  private fun createAccountKeyPairs(numAccounts: Int): List<KeyPair> = runBlocking {
    (0 until numAccounts).map {
      KeyFactory.createKeyPairFromMnemonic(
        KeyFactory.createMnemonic(MnemonicPhraseLength.TWENTY_FOUR)
      )
    }
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