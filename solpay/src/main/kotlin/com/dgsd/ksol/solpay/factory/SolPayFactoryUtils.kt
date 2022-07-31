package com.dgsd.ksol.solpay.factory

import com.dgsd.ksol.solpay.model.SolPayParsingException

internal fun validateUrlLength(url: String) {
  if (url.length > SolPayConstants.MAX_URL_LENGTH) {
    throw SolPayParsingException(url, "URL too long")
  }
}

internal fun validateSolanaScheme(url: String) {
  if (!url.startsWith(SolPayConstants.SCHEME_SOLANA + ":")) {
    throw SolPayParsingException(url, "Invalid scheme")
  }
}