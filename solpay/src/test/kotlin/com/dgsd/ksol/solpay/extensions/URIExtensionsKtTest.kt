package com.dgsd.ksol.solpay.extensions

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.net.URI

class URIExtensionsKtTest {

  @Test
  fun getPathName_whenEmpty_returnsEmpty() {
    val input = ""

    val pathName = URI.create(input).getPathName()

    Assertions.assertEquals("", pathName)
  }

  @Test
  fun getPathName_withNoQueryParam_returnsFullPath() {
    val input = "solana:abc123"

    val pathName = URI.create(input).getPathName()

    Assertions.assertEquals("abc123", pathName)
  }

  @Test
  fun getPathName_withMultipleParts_returnsFullPath() {
    val input = "solana:abc/123/"

    val pathName = URI.create(input).getPathName()

    Assertions.assertEquals("abc/123/", pathName)
  }

  @Test
  fun getPathName_withQueryParam_returnsFullPath() {
    val input = "solana:abc123?a=1&b=2"

    val pathName = URI.create(input).getPathName()

    Assertions.assertEquals("abc123", pathName)
  }

  @Test
  fun getPathName_withMultiplePartsAndQueryParams_returnsFullPath() {
    val input = "solana:abc/123/?a=1&b=2"

    val pathName = URI.create(input).getPathName()

    Assertions.assertEquals("abc/123/", pathName)
  }

  @Test
  fun getPathName_withTrailingQuestionMark_returnsFullPath() {
    val input = "solana:abc123?"

    val pathName = URI.create(input).getPathName()

    Assertions.assertEquals("abc123", pathName)
  }


  @Test
  fun getRawQueryParameters_withOneOfEach_extractsCorrectQueryParameters() {
    val input = URI.create("http://example.com?a=1&b=2&c=3")
    val params = input.getRawQueryParameters()

    Assertions.assertEquals(3, params.size)
    Assertions.assertEquals("1", params.getValue("a").single())
    Assertions.assertEquals("2", params.getValue("b").single())
    Assertions.assertEquals("3", params.getValue("c").single())
  }

  @Test
  fun getRawQueryParameters_withOneOfMultipleForSameKey_extractsCorrectQueryParameters() {
    val input = URI.create("http://example.com?a=1&b=2&c=3&c=4&c=5")
    val params = input.getRawQueryParameters()

    Assertions.assertEquals(3, params.size)
    Assertions.assertEquals("1", params.getValue("a").single())
    Assertions.assertEquals("2", params.getValue("b").single())
    Assertions.assertIterableEquals(listOf("3", "4", "5"), params.getValue("c"))
  }

  @Test
  fun getRawQueryParameters_withMissingValue_extractsCorrectQueryParameters() {
    val input = URI.create("http://example.com?a&b=2&c=3")
    val params = input.getRawQueryParameters()

    Assertions.assertEquals(3, params.size)
    Assertions.assertEquals("", params.getValue("a").single())
    Assertions.assertEquals("2", params.getValue("b").single())
    Assertions.assertEquals("3", params.getValue("c").single())
  }

  @Test
  fun getRawQueryParameters_withMissingValueAndMultiple_extractsCorrectQueryParameters() {
    val input = URI.create("http://example.com?a&a=1&b=2&c=3")
    val params = input.getRawQueryParameters()

    Assertions.assertEquals(3, params.size)
    Assertions.assertIterableEquals(listOf("", "1"), params.getValue("a"))
    Assertions.assertEquals("2", params.getValue("b").single())
    Assertions.assertEquals("3", params.getValue("c").single())
  }

  @Test
  fun getRawQueryParameters_withMissingQuery_extractsCorrectQueryParameters() {
    val input = URI.create("http://example.com")
    val params = input.getRawQueryParameters()

    Assertions.assertEquals(0, params.size)
  }

  @Test
  fun getRawQueryParameters_withMissingQueryButTrailingQuestionMark_extractsCorrectQueryParameters() {
    val input = URI.create("http://example.com?")
    val params = input.getRawQueryParameters()

    Assertions.assertEquals(0, params.size)
  }
}