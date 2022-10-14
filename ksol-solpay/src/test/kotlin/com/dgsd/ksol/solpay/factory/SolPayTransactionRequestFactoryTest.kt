package com.dgsd.ksol.solpay.factory

import com.dgsd.ksol.solpay.model.SolPayParsingException
import com.dgsd.ksol.solpay.model.SolPayTransactionRequest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SolPayTransactionRequestFactoryTest {

  @Test
  fun createUrl_withLinkThatNeedsUrlEncoding_isEncoded() {
    val input = SolPayTransactionRequest("https://example.com?a=b")

    val output = SolPayTransactionRequestFactory.createUrl(input)

    Assertions.assertEquals(
      "solana:https%3A%2F%2Fexample.com%3Fa%3Db",
      output
    )
  }

  @Test
  fun createUrl_withLinkThatHasNoQueryParams_isNotEncoded() {
    val input = SolPayTransactionRequest("https://example.com")

    val output = SolPayTransactionRequestFactory.createUrl(input)

    Assertions.assertEquals("solana:https://example.com", output)
  }

  @Test
  fun createRequest_withNoSolanaScheme_throwsException() {
    Assertions.assertThrows(SolPayParsingException::class.java) {
      val input = "https://example.com"
      SolPayTransactionRequestFactory.createRequest(input)
    }
  }

  @Test
  fun createRequest_withNonHttpsLink_throwsException() {
    Assertions.assertThrows(SolPayParsingException::class.java) {
      val input = "solana:http://example.com"
      SolPayTransactionRequestFactory.createRequest(input)
    }
  }

  @Test
  fun createRequest_withUrlEncodedLink_parsesCorrectly() {
    val input = "solana:https%3A%2F%2Fexample.com%3Fa%3Db"

    val output = SolPayTransactionRequestFactory.createRequest(input)

    Assertions.assertEquals(
      "https://example.com?a=b",
      output.link
    )
  }

  @Test
  fun createRequest_withNonUrlEncodedLink_parsesCorrectly() {
    val input = "solana:https://example.com"

    val output = SolPayTransactionRequestFactory.createRequest(input)

    Assertions.assertEquals(
      "https://example.com",
      output.link
    )
  }
}