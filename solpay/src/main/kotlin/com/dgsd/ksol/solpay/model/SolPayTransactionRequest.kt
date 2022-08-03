package com.dgsd.ksol.solpay.model

data class SolPayTransactionRequest(

  /**
   * A URL-encoded absolute HTTPS url
   */
  val link: String,
) : SolPayRequest