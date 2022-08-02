package com.dgsd.ksol.solpay.model

/**
 * Represents the details of a [SolPayTransactionRequest] that should be displayed to a user.
 */
data class SolPayTransactionRequestDetails internal constructor(

  /**
   * Describes the source of the transaction request.
   *
   * For example, this might be the name of a brand, store, application, or person making the request
   */
  val label: String?,

  /**
   * Absolute HTTP or HTTPS URL of an icon image. The file must be an SVG, PNG, or WebP image
   */
  val iconUrl: String?,
)