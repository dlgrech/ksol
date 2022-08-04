package com.dgsd.ksol.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class LamportKtTest {

  @Test
  fun asSolAmount_withMinValue_returnsCorrectSolAmount() {
    val input = 1L
    val output = input.asSolAmount()
    Assertions.assertEquals(SOL_IN_LAMPORTS, output)
  }

  @Test
  fun asSolAmount_withFractionalValue_returnsCorrectSolAmount() {
    val input = 10L
    val output = input.asSolAmount()
    Assertions.assertEquals(BigDecimal("0.000000010"), output)
  }

  @Test
  fun asSolAmount_withWholeValue_returnsCorrectSolAmount() {
    val input = 1000000000L
    val output = input.asSolAmount()
    Assertions.assertEquals(LAMPORTS_IN_SOL * SOL_IN_LAMPORTS, output)
  }
}