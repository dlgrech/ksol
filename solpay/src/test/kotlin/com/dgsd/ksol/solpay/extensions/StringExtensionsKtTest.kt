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

  @Test
  fun getPathName_whenEmpty_returnsEmpty() {
    val input = ""

    val pathName = input.getPathPortion()

    Assertions.assertEquals("", pathName)
  }

  @Test
  fun getPathName_withNoQueryParam_returnsFullPath() {
    val input = "solana:abc123"

    val pathName = input.getPathPortion()

    Assertions.assertEquals("abc123", pathName)
  }

  @Test
  fun getPathName_withMultipleParts_returnsFullPath() {
    val input = "solana:abc/123/"

    val pathName = input.getPathPortion()

    Assertions.assertEquals("abc/123/", pathName)
  }

  @Test
  fun getPathName_withQueryParam_returnsFullPath() {
    val input = "solana:abc123?a=1&b=2"

    val pathName = input.getPathPortion()

    Assertions.assertEquals("abc123", pathName)
  }

  @Test
  fun getPathName_withMultiplePartsAndQueryParams_returnsFullPath() {
    val input = "solana:abc/123/?a=1&b=2"

    val pathName = input.getPathPortion()

    Assertions.assertEquals("abc/123/", pathName)
  }

  @Test
  fun getPathName_withTrailingQuestionMark_returnsFullPath() {
    val input = "solana:abc123?"

    val pathName = input.getPathPortion()

    Assertions.assertEquals("abc123", pathName)
  }


  @Test
  fun getRawQueryParameters_withOneOfEach_extractsCorrectQueryParameters() {
    val input = "http://example.com?a=1&b=2&c=3"
    val params = input.getRawQueryParameters()

    Assertions.assertEquals(3, params.size)
    Assertions.assertEquals("1", params.getValue("a").single())
    Assertions.assertEquals("2", params.getValue("b").single())
    Assertions.assertEquals("3", params.getValue("c").single())
  }

  @Test
  fun getRawQueryParameters_withOneOfMultipleForSameKey_extractsCorrectQueryParameters() {
    val input = "http://example.com?a=1&b=2&c=3&c=4&c=5"
    val params = input.getRawQueryParameters()

    Assertions.assertEquals(3, params.size)
    Assertions.assertEquals("1", params.getValue("a").single())
    Assertions.assertEquals("2", params.getValue("b").single())
    Assertions.assertIterableEquals(listOf("3", "4", "5"), params.getValue("c"))
  }

  @Test
  fun getRawQueryParameters_withMissingValue_extractsCorrectQueryParameters() {
    val input = "http://example.com?a&b=2&c=3"
    val params = input.getRawQueryParameters()

    Assertions.assertEquals(3, params.size)
    Assertions.assertEquals("", params.getValue("a").single())
    Assertions.assertEquals("2", params.getValue("b").single())
    Assertions.assertEquals("3", params.getValue("c").single())
  }

  @Test
  fun getRawQueryParameters_withMissingValueAndMultiple_extractsCorrectQueryParameters() {
    val input = "http://example.com?a&a=1&b=2&c=3"
    val params = input.getRawQueryParameters()

    Assertions.assertEquals(3, params.size)
    Assertions.assertIterableEquals(listOf("", "1"), params.getValue("a"))
    Assertions.assertEquals("2", params.getValue("b").single())
    Assertions.assertEquals("3", params.getValue("c").single())
  }

  @Test
  fun getRawQueryParameters_withMissingQuery_extractsCorrectQueryParameters() {
    val input = "http://example.com"
    val params = input.getRawQueryParameters()

    Assertions.assertEquals(0, params.size)
  }

  @Test
  fun getRawQueryParameters_withMissingQueryButTrailingQuestionMark_extractsCorrectQueryParameters() {
    val input = "http://example.com?"
    val params = input.getRawQueryParameters()

    Assertions.assertEquals(0, params.size)
  }
}