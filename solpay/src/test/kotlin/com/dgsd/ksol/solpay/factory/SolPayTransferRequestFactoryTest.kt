package com.dgsd.ksol.solpay.factory

import com.dgsd.ksol.model.PublicKey
import com.dgsd.ksol.solpay.model.SolPayParsingException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class SolPayTransferRequestFactoryTest {

  @Test
  fun create_withEmptyUrl_throwsError() {
    Assertions.assertThrows(SolPayParsingException::class.java) {
      val input = ""
      SolPayTransferRequestFactory.createRequest(input)
    }
  }

  @Test
  fun create_withInvalidUrl_throwsError() {
    Assertions.assertThrows(SolPayParsingException::class.java) {
      val input = "this is not a url"
      SolPayTransferRequestFactory.createRequest(input)
    }
  }

  @Test
  fun create_withHttpUrl_throwsError() {
    Assertions.assertThrows(SolPayParsingException::class.java) {
      val input = "http://example.com"
      SolPayTransferRequestFactory.createRequest(input)
    }
  }

  @Test
  fun create_withUrlTooLong_throwsError() {
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
  fun create_withInvalidRecipient_throwsError() {
    Assertions.assertThrows(SolPayParsingException::class.java) {
      val input = "solana:abc123"
      SolPayTransferRequestFactory.createRequest(input)
    }
  }

  @Test
  fun create_withNoExtraParams_isParsedCorrectly() {
    val input = "solana:9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ"

    val request = SolPayTransferRequestFactory.createRequest(input)

    Assertions.assertEquals(
      PublicKey.fromBase58("9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ"),
      request.recipient
    )
  }

  @Test
  fun create_withAllExtraParams_isParsedCorrectly() {
    val input = buildString {
      append("solana:9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ?")
      append("amount=0.1&")
      append("spl-token=EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v&")
      append("reference=9B5XszUGdMaxCZ7uSQhPzdks5ZQSmWxrmzCSvtJ6Ns6g&")
      append("reference=11111111111111111111111111111111&")
      append("message=Thanks%20for%20all%20the%20fish&")
      append("memo=Order%20Id%2012345&")
      append("label=Cool%20Label")
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
  fun create_withDecimalAmount_isParsedCorrectly() {
    val input = "solana:9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ?amount=0.1"

    val request = SolPayTransferRequestFactory.createRequest(input)

    Assertions.assertEquals(
      PublicKey.fromBase58("9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ"),
      request.recipient
    )

    Assertions.assertEquals(BigDecimal("0.1"), request.amount)
  }

  @Test
  fun create_withInvalidDecimalAmount_throwsError() {
    Assertions.assertThrows(SolPayParsingException::class.java) {
      val input = "solana:9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ?amount=0.0000000001"
      SolPayTransferRequestFactory.createRequest(input)
    }
  }

  @Test
  fun create_withNegativeAmount_throwsError() {
    Assertions.assertThrows(SolPayParsingException::class.java) {
      val input = "solana:9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ?amount=-1"
      SolPayTransferRequestFactory.createRequest(input)
    }
  }

  @Test
  fun create_withIntegerAmount_isParsedCorrectly() {
    val input = "solana:9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ?amount=1"

    val request = SolPayTransferRequestFactory.createRequest(input)

    Assertions.assertEquals(
      PublicKey.fromBase58("9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ"),
      request.recipient
    )

    Assertions.assertEquals(BigDecimal("1"), request.amount)
  }

  @Test
  fun create_withZeroAmount_isParsedCorrectly() {
    val input = "solana:9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ?amount=0"

    val request = SolPayTransferRequestFactory.createRequest(input)

    Assertions.assertEquals(
      PublicKey.fromBase58("9nRgWwaeutVYbGFR1yC4TxBHY72LQkPxbTmEFvLKgrKJ"),
      request.recipient
    )

    Assertions.assertEquals(BigDecimal.ZERO, request.amount)
  }
}