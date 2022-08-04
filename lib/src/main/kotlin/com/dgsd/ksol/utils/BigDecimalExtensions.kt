package com.dgsd.ksol.utils

import com.dgsd.ksol.model.LAMPORTS_IN_SOL
import com.dgsd.ksol.model.Lamports
import com.dgsd.ksol.model.SOL_IN_LAMPORTS
import java.math.BigDecimal

/**
 * @return `true` if this [BigDecimal] represents a valid number of SOL tokens, or `false`
 * if it does not (such as when having too many decimals)
 */
fun BigDecimal.isValidSolAmount(): Boolean {
  return if (this < BigDecimal.ZERO) {
    return false
  } else {
    scale() <= SOL_IN_LAMPORTS.scale()
  }
}

/**
 * Converts the value of this [BigDecimal], which is presumed to be a valid SOL amount
 * (as determined by [isValidSolAmount]) to an equivalent [Lamports] value
 */
fun BigDecimal.solToLamports(): Lamports {
  return (this * LAMPORTS_IN_SOL).longValueExact()
}