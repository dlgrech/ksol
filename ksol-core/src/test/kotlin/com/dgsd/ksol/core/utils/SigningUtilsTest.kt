package com.dgsd.ksol.core.utils

import com.dgsd.ksol.core.model.KeyPair
import com.dgsd.ksol.core.model.PrivateKey
import com.dgsd.ksol.core.model.PublicKey
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SigningUtilsTest {


  @Test
  fun isValidSignature_whenValid_returnsTrue()  {
    val keyPair = KeyPair(
      publicKey = PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"),
      privateKey = PrivateKey.fromBase58(
        "5ysPKzei6U5b1KTRs7XjwUL7335E8L1eta531oQkXP63Wf1jkavEyov1zyNX928hHhNkfEpVptACSfWPZtbzgeoa"
      )
    )

    val inputData = byteArrayOf(1, 2, 3, 4)

    val signature = SigningUtils.sign(inputData, keyPair.privateKey)

    Assertions.assertTrue(
      SigningUtils.isValidSignature(inputData, signature, keyPair.publicKey)
    )
  }

  @Test
  fun isValidSignature_whenDifferentInputData_returnsFalse() {
    val keyPair = KeyPair(
      publicKey = PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"),
      privateKey = PrivateKey.fromBase58(
        "5ysPKzei6U5b1KTRs7XjwUL7335E8L1eta531oQkXP63Wf1jkavEyov1zyNX928hHhNkfEpVptACSfWPZtbzgeoa"
      )
    )

    val inputData = byteArrayOf(1, 2, 3, 4)

    val signature = SigningUtils.sign(inputData, keyPair.privateKey)

    Assertions.assertFalse(
      SigningUtils.isValidSignature(byteArrayOf(4, 5, 6, 7), signature, keyPair.publicKey)
    )
  }

  @Test
  fun isValidSignature_whenMismatchedKey_returnsFalse() {
    val privateKeyUsedToSign = PrivateKey.fromBase58(
      "5ysPKzei6U5b1KTRs7XjwUL7335E8L1eta531oQkXP63Wf1jkavEyov1zyNX928hHhNkfEpVptACSfWPZtbzgeoa"
    )
    val publicKeyUsedToCheck = PublicKey.fromBase58("5RexpHu5cHhLmNH9Kr22q7Kpybg1RRV41ukN1zh1vDK9")

    val inputData = byteArrayOf(1, 2, 3, 4)

    val signature = SigningUtils.sign(inputData, privateKeyUsedToSign)

    Assertions.assertFalse(
      SigningUtils.isValidSignature(inputData, signature, publicKeyUsedToCheck)
    )
  }
}