package com.dgsd.ksol.solpay.model

import com.dgsd.ksol.core.model.PublicKey
import java.math.BigDecimal

data class SolPayTransferRequest(

  /**
   * A single recipient field is required as the pathname.
   *
   * The value must be the base58-encoded public key of a native SOL account.
   */
  val recipient: PublicKey,

  /**
   * An optional amount to request.
   *
   * If a value is not provided, the wallet must prompt the user for the amount
   */
  val amount: BigDecimal? = null,

  /**
   *  The base58-encoded public key of an SPL Token mint account.
   *
   *  If the field is null, the request describes a native SOL transfer
   */
  val splTokenMintAccount: PublicKey? = null,


  /**
   * Must include them in the order provided as read-only, non-signer keys to the transfer
   * transaction instruction
   */
  val references: List<PublicKey> = emptyList(),

  /**
   * Describes the source of the transfer request.
   *
   * For example, this might be the name of a brand, store, application, or person
   * making the request.
   */
  val label: String? = null,

  /**
   * Describes the nature of the transfer request.
   *
   * For example, this might be the name of an item being purchased, an order ID,
   * or a thank you note
   */
  val message: String? = null,

  /**
   * Should be included in an SPL Memo instruction in the payment transaction.
   *
   * The memo will be recorded by validators and should not include private or
   * sensitive information.
   */
  val memo: String? = null
): SolPayRequest