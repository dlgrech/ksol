package com.dgsd.ksol.serialization

import com.dgsd.ksol.model.PublicKey
import com.dgsd.ksol.model.TransactionAccountMetadata
import com.dgsd.ksol.utils.DecodingUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LocalTransactionSerializationTest {

  @Test
  fun deserialize_withKnownTransaction() {
    val transactionBytes = DecodingUtils.decodeBase64(
      "AVuErQHaXv0SG0/PchunfxHKt8wMRfMZzqV0tkC5qO6owYxWU2v871AoWywGoFQr4z+q/7mE8lIufNl/kxj+nQ0BAAEDE5j2LG0aRXxRumpLXz29L2n8qTIWIY3ImX5Ba9F9k8r9Q5/Mtmcn8onFxt47xKj+XdXXd3C8j/FcPu7csUrz/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAxJrndgN4IFTxep3s6kO0ROug7bEsbx0xxuDkqEvwUusBAgIAAQwCAAAAMQAAAAAAAAA="
    )

    val transaction = LocalTransactionSerialization.deserialize(transactionBytes)

    Assertions.assertEquals(
      "2q8Fs6wmDHcdNKJnVJoXYnDVqrWm8GYsxYt2QwUPvadkEQXinWJRwWVzxdUmJY9YH8iZETcXjk3ZdBcDUkuUtfKz",
      transaction.signatures.single()
    )

    Assertions.assertEquals(
      "EETubP5AKHgjPAhzPAFcb8BAY1hMH639CWCFTqi3hq1k",
      transaction.message.recentBlockhash.toBase58String()
    )

    Assertions.assertEquals(1, transaction.message.header.numRequiredSignatures)
    Assertions.assertEquals(0, transaction.message.header.numReadonlySignedAccounts)
    Assertions.assertEquals(1, transaction.message.header.numReadonlyUnsignedAccounts)

    Assertions.assertEquals(
      "11111111111111111111111111111111",
      transaction.message.instructions.single().programAccount.toBase58String()
    )
    Assertions.assertEquals(
      listOf(
        TransactionAccountMetadata(
          publicKey = PublicKey.fromBase58("2KW2XRd9kwqet15Aha2oK3tYvd3nWbTFH1MBiRAv1BE1"),
          isSigner = true,
          isFeePayer = true,
          isWritable = true
        ),
        TransactionAccountMetadata(
          publicKey = PublicKey.fromBase58("J3dxNj7nDRRqRRXuEMynDG57DkZK4jYRuv3Garmb1i99"),
          isSigner = false,
          isFeePayer = false,
          isWritable = true
        ),
        TransactionAccountMetadata(
          publicKey = PublicKey.fromBase58("11111111111111111111111111111111"),
          isSigner = false,
          isFeePayer = false,
          isWritable = false
        ),
      ),
      transaction.message.accountKeys,
    )

    Assertions.assertEquals(
      listOf(
        "2KW2XRd9kwqet15Aha2oK3tYvd3nWbTFH1MBiRAv1BE1",
        "J3dxNj7nDRRqRRXuEMynDG57DkZK4jYRuv3Garmb1i99"
      ),
      transaction.message.instructions.single().inputAccounts.map { it.toBase58String() }
    )
  }
}