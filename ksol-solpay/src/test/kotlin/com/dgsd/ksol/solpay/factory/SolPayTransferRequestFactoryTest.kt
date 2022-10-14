package com.dgsd.ksol.solpay.factory

import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.solpay.model.SolPayParsingException
import com.dgsd.ksol.solpay.model.SolPayTransferRequest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class SolPayTransferRequestFactoryTest {

  @Test
  fun createRequest_withEmptyUrl_throwsError() {
    Assertions.assertThrows(SolPayParsingException::class.java) {
      val input = ""
      SolPayTransferRequestFactory.createRequest(input)
    }
  }

  @Test
  fun createRequest_withInvalidUrl_throwsError() {
    Assertions.assertThrows(SolPayParsingException::class.java) {
      val input = "this is not a url"
      SolPayTransferRequestFactory.createRequest(input)
    }
  }

  @Test
  fun createRequest_withHttpUrl_throwsError() {
    Assertions.assertThrows(SolPayParsingException::class.java) {
      val input = "http://example.com"
      SolPayTransferRequestFactory.createRequest(input)
    }
  }

  @Test
  fun createRequest_withUrlTooLong_throwsError() {
    Assertions.assertThrows(SolPayParsingException::class.java) {
      val input = buildString {
        repeat(SolPayConstants.MAX_URL_LENGTH + 1) {
          append('0')
        }
      }

      SolPayTransferRequestFactory.createRequest(input)
    }
  }

  @Test
  fun createRequest_withInvalidRecipient_throwsError() {
    Assertions.assertThrows(SolPayParsingException::class.java) {
      val input = "solana:abc123"
      SolPayTransferRequestFactory.createRequest(input)
    }
  }

  @Test
  fun createRequest_withNoExtraParams_isParsedCorrectly() {
    val input = "solana:9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ"

    val request = SolPayTransferRequestFactory.createRequest(input)

    Assertions.assertEquals(
      PublicKey.fromBase58("9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ"),
      request.recipient
    )
  }

  @Test
  fun createRequest_withNoExtraParamsAndTrailingQuestionMark_isParsedCorrectly() {
    val input = "solana:9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ?"

    val request = SolPayTransferRequestFactory.createRequest(input)

    Assertions.assertEquals(
      PublicKey.fromBase58("9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ"),
      request.recipient
    )
  }

  @Test
  fun createRequest_withAllExtraParams_isParsedCorrectly() {
    val input = buildString {
      append("solana:9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ?")
      append("amount=0.1&")
      append("spl-token=EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v&")
      append("reference=9B5XszUGdMaxCZ7uSQhPzdks5ZQSmWxrmzCSvtJ6Ns6g&")
      append("reference=11111111111111111111111111111111&")
      append("label=Cool%20Label&")
      append("message=Thanks%20for%20all%20the%20fish&")
      append("memo=Order%20Id%2012345")
    }

    val request = SolPayTransferRequestFactory.createRequest(input)

    Assertions.assertEquals(
      PublicKey.fromBase58("9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ"),
      request.recipient
    )
    Assertions.assertEquals(
      PublicKey.fromBase58("EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v"),
      request.splTokenMintAccount
    )
    Assertions.assertEquals("Cool Label", request.label)
    Assertions.assertEquals("Order Id 12345", request.memo)
    Assertions.assertEquals("Thanks for all the fish", request.message)
    Assertions.assertEquals(BigDecimal("0.1"), request.amount)
    Assertions.assertIterableEquals(
      listOf(
        PublicKey.fromBase58("9B5XszUGdMaxCZ7uSQhPzdks5ZQSmWxrmzCSvtJ6Ns6g"),
        PublicKey.fromBase58("11111111111111111111111111111111"),
      ),
      request.references
    )
  }

  @Test
  fun createRequest_withDecimalAmount_isParsedCorrectly() {
    val input = "solana:9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ?amount=0.1"

    val request = SolPayTransferRequestFactory.createRequest(input)

    Assertions.assertEquals(
      PublicKey.fromBase58("9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ"),
      request.recipient
    )

    Assertions.assertEquals(BigDecimal("0.1"), request.amount)
  }

  @Test
  fun createRequest_withInvalidDecimalAmount_throwsError() {
    Assertions.assertThrows(SolPayParsingException::class.java) {
      val input = "solana:9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ?amount=0.0000000001"
      SolPayTransferRequestFactory.createRequest(input)
    }
  }

  @Test
  fun createRequest_withNegativeAmount_throwsError() {
    Assertions.assertThrows(SolPayParsingException::class.java) {
      val input = "solana:9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ?amount=-1"
      SolPayTransferRequestFactory.createRequest(input)
    }
  }

  @Test
  fun createRequest_withIntegerAmount_isParsedCorrectly() {
    val input = "solana:9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ?amount=1"

    val request = SolPayTransferRequestFactory.createRequest(input)

    Assertions.assertEquals(
      PublicKey.fromBase58("9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ"),
      request.recipient
    )

    Assertions.assertEquals(BigDecimal("1"), request.amount)
  }

  @Test
  fun createRequest_withZeroAmount_isParsedCorrectly() {
    val input = "solana:9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ?amount=0"

    val request = SolPayTransferRequestFactory.createRequest(input)

    Assertions.assertEquals(
      PublicKey.fromBase58("9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ"),
      request.recipient
    )

    Assertions.assertEquals(BigDecimal.ZERO, request.amount)
  }

  @Test
  fun createUrl_withAllExtraParams_createsExpectedUrl() {
    val request = SolPayTransferRequest(
      recipient = PublicKey.fromBase58("9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ"),
      splTokenMintAccount = PublicKey.fromBase58("EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v"),
      references = listOf(
        PublicKey.fromBase58("9B5XszUGdMaxCZ7uSQhPzdks5ZQSmWxrmzCSvtJ6Ns6g"),
        PublicKey.fromBase58("11111111111111111111111111111111"),
      ),
      amount = BigDecimal("1.5"),
      message = "Thanks for all the fish",
      memo = "Order Id 12345",
      label = "Cool Label"
    )

    val url = SolPayTransferRequestFactory.createUrl(request)

    val expected = buildString {
      append("solana:9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ?")
      append("amount=1.5&")
      append("spl-token=EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v&")
      append("reference=9B5XszUGdMaxCZ7uSQhPzdks5ZQSmWxrmzCSvtJ6Ns6g&")
      append("reference=11111111111111111111111111111111&")
      append("label=Cool%20Label&")
      append("message=Thanks%20for%20all%20the%20fish&")
      append("memo=Order%20Id%2012345")
    }

    Assertions.assertEquals(expected, url)
  }

  @Test
  fun createUrl_withOnlyAmount_createsExpectedUrl() {
    val request = SolPayTransferRequest(
      recipient = PublicKey.fromBase58("9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ"),
      amount = BigDecimal("1.5"),
    )

    val url = SolPayTransferRequestFactory.createUrl(request)

    Assertions.assertEquals(
      "solana:9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ?amount=1.5",
      url
    )
  }

  @Test
  fun createUrl_withNoQueryParmas_createsExpectedUrl() {
    val request = SolPayTransferRequest(
      recipient = PublicKey.fromBase58("9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ"),
    )

    val url = SolPayTransferRequestFactory.createUrl(request)

    Assertions.assertEquals(
      "solana:9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ",
      url
    )
  }
}