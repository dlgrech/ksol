package com.dgsd.ksol.core.utils

import com.dgsd.ksol.core.model.PrivateKey
import com.dgsd.ksol.core.model.PublicKey
import com.iwebpp.crypto.TweetNaclFast

internal object SigningUtils {

  /**
   * Signs the input data using the given key and returns a signature.
   */
  fun sign(inputData: ByteArray, key: PrivateKey): ByteArray {
    return TweetNaclFast.Signature(
      byteArrayOf(),
      key.key
    ).detached(inputData)
  }

  /**
   * Validate that the given `inputData`, when signed with the private key matching `signer`,
   * would result in the given `signature`
   */
  fun isValidSignature(inputData: ByteArray, signature: ByteArray, signer: PublicKey): Boolean {
    return TweetNaclFast.Signature(
      signer.key,
      byteArrayOf()
    ).detached_verify(inputData, signature)
  }
}