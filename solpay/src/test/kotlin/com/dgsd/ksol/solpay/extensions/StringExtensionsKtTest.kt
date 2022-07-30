package com.dgsd.ksol.solpay.extensions

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class StringExtensionsKtTest {

  @Test
  fun urlEncode_withCharactersThatNeedEscaping_areEncoded() {
    val input = "Thanks for all the fish"

    val output = input.urlEncode()

    Assertions.assertEquals("Thanks%20for%20all%20the%20fish", output)
  }

  @Test
  fun urlEncode_withCharactersThatDoNotNeedEscaping_areEncoded() {
    val input = "ThanksForAllTheFish"

    val output = input.urlEncode()

    Assertions.assertEquals(input, output)
  }

  @Test
  fun urlDecode_withCharactersThatNeedEscaping_areDecoded() {
    val input = "Thanks%20for%20all%20the%20fish"

    val output = input.urlDecode()

    Assertions.assertEquals("Thanks for all the fish", output)
  }

  @Test
  fun urlDecode_withCharactersThatDoNotNeedEscaping_areDecoded() {
    val input = "ThanksForAllTheFish"

    val output = input.urlDecode()

    Assertions.assertEquals(input, output)
  }
}