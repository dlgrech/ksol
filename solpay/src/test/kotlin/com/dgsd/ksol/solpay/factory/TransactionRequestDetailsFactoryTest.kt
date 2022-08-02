package com.dgsd.ksol.solpay.factory

import com.dgsd.ksol.solpay.api.TransactionRequestGetDetailsResponse
import com.dgsd.ksol.solpay.model.SolPayTransactionRequestDetails
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TransactionRequestDetailsFactoryTest {

  @Test
  fun create_withInvalidIconUrl_throwsException() {
    val response = TransactionRequestGetDetailsResponse(
      label = "label",
      iconUrl = "example.com/test.png"
    )

    Assertions.assertThrows(IllegalStateException::class.java) {
      TransactionRequestDetailsFactory.create(response)
    }
  }

  @Test
  fun create_withValidIconUrl_createsInstance() {
    val response = TransactionRequestGetDetailsResponse(
      label = "label",
      iconUrl = "https://example.com/test.png"
    )

    val output = TransactionRequestDetailsFactory.create(response)

    Assertions.assertEquals(
      SolPayTransactionRequestDetails(
        label = "label",
        iconUrl = "https://example.com/test.png"
      ),
      output
    )
  }

  @Test
  fun create_withMissingIconUrl_createsInstance() {
    val response = TransactionRequestGetDetailsResponse(
      label = "label",
      iconUrl = null
    )

    val output = TransactionRequestDetailsFactory.create(response)

    Assertions.assertEquals(
      SolPayTransactionRequestDetails(
        label = "label",
        iconUrl = null
      ),
      output
    )
  }
}