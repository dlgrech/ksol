package com.dgsd.ksol.utils

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