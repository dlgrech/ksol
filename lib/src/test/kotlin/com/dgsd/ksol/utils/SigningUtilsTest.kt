package com.dgsd.ksol.utils

import com.dgsd.ksol.keygen.KeyFactory
import com.dgsd.ksol.keygen.MnemonicPhraseLength
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SigningUtilsTest {


  @Test
  fun isValidSignature_whenValid_returnsTrue() = runBlocking {
    val keyPair = KeyFactory.createKeyPairFromMnemonic(
      KeyFactory.createMnemonic(MnemonicPhraseLength.TWENTY_FOUR)
    )

    val inputData = byteArrayOf(1, 2, 3, 4)

    val signature = SigningUtils.sign(inputData, keyPair.privateKey)

    Assertions.assertTrue(
      SigningUtils.isValidSignature(inputData, signature, keyPair.publicKey)
    )
  }

  @Test
  fun isValidSignature_whenDifferentInputData_returnsFalse() = runBlocking {
    val keyPair = KeyFactory.createKeyPairFromMnemonic(
      KeyFactory.createMnemonic(MnemonicPhraseLength.TWENTY_FOUR)
    )

    val inputData = byteArrayOf(1, 2, 3, 4)

    val signature = SigningUtils.sign(inputData, keyPair.privateKey)

    Assertions.assertFalse(
      SigningUtils.isValidSignature(byteArrayOf(4, 5, 6, 7), signature, keyPair.publicKey)
    )
  }

  @Test
  fun isValidSignature_whenMismatchedKey_returnsFalse() = runBlocking {
    val keyPairUsedToSign = KeyFactory.createKeyPairFromMnemonic(
      KeyFactory.createMnemonic(MnemonicPhraseLength.TWENTY_FOUR)
    )
    val keyPairToCheck = KeyFactory.createKeyPairFromMnemonic(
      KeyFactory.createMnemonic(MnemonicPhraseLength.TWENTY_FOUR)
    )

    val inputData = byteArrayOf(1, 2, 3, 4)

    val signature = SigningUtils.sign(inputData, keyPairUsedToSign.privateKey)

    Assertions.assertFalse(
      SigningUtils.isValidSignature(inputData, signature, keyPairToCheck.publicKey)
    )
  }
}