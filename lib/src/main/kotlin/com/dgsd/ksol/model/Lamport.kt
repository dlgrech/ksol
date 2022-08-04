package com.dgsd.ksol.model

import java.math.BigDecimal

/**
 * Represents a fraction of a single SOL native token.
 *
 * A single lamport has a value of 0.000000001 SOL.
 */
typealias Lamports = Long

/**
 * The number of SOL tokens in a single lamport.
 */
val SOL_IN_LAMPORTS = BigDecimal("0.000000001")

/**
 * The number of lamports that make up 1 SOL token
 */
val LAMPORTS_IN_SOL = BigDecimal.ONE.div(SOL_IN_LAMPORTS)

/**
 * @return this [Lamports] as a fractional SOL amount
 */
fun Lamports.asSolAmount(): BigDecimal {
  return SOL_IN_LAMPORTS * BigDecimal.valueOf(this)
}