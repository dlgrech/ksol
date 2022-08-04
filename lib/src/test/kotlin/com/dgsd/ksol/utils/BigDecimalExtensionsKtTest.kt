package com.dgsd.ksol.utils

import com.dgsd.ksol.model.SOL_IN_LAMPORTS
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class BigDecimalExtensionsKtTest {

  @Test
  fun isValidSolAmount_withNoDecimals_isValid() {
    val input = BigDecimal.TEN
    val output = input.isValidSolAmount()
    Assertions.assertTrue(output)
  }

  @Test
  fun isValidSolAmount_withValidNumberOfDecimals_isValid() {
    val input = BigDecimal("1.99999")
    val output = input.isValidSolAmount()
    Assertions.assertTrue(output)
  }

  @Test
  fun isValidSolAmount_withValidNumberOfDecimalsAndLessThan1_isValid() {
    val input = BigDecimal("0.99999")
    val output = input.isValidSolAmount()
    Assertions.assertTrue(output)
  }

  @Test
  fun isValidSolAmount_withTooManyDecimalsAndAboveOne_isNotValid() {
    val input = BigDecimal("5.97549168946")
    val output = input.isValidSolAmount()
    Assertions.assertFalse(output)
  }

  @Test
  fun isValidSolAmount_withTooManyDecimals_isNotValid() {
    val input = BigDecimal("0.0000000001")
    val output = input.isValidSolAmount()
    Assertions.assertFalse(output)
  }


  @Test
  fun isValidSolAmount_withNegativeAmount_isNotValid() {
    val input = BigDecimal("-1.5")
    val output = input.isValidSolAmount()
    Assertions.assertFalse(output)
  }

  @Test
  fun isValidSolAmount_withZero_isValid() {
    val input = BigDecimal.ZERO
    val output = input.isValidSolAmount()
    Assertions.assertTrue(output)
  }

  @Test
  fun isValidSolAmount_withMinLamportAmount_isValid() {
    val input = SOL_IN_LAMPORTS
    val output = input.isValidSolAmount()
    Assertions.assertTrue(output)
  }

  @Test
  fun solToLamports_withValidSOLValue_convertsToLamports() {
    val input = BigDecimal("1.5")
    val output = input.solToLamports()

    Assertions.assertEquals(1500000000, output)
  }

  @Test
  fun solToLamports_withMinSOLValue_convertsToLamports() {
    val input = SOL_IN_LAMPORTS
    val output = input.solToLamports()

    Assertions.assertEquals(1, output)
  }

  @Test
  fun solToLamports_withInvalidSOLValue_throwsException() {
    val input = BigDecimal("0.000000000001")
    Assertions.assertThrows(ArithmeticException::class.java) {
      input.solToLamports()
    }
  }
}