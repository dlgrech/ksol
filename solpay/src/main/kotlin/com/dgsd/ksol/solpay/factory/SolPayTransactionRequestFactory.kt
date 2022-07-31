package com.dgsd.ksol.solpay.factory

import com.dgsd.ksol.solpay.extensions.getPathPortion
import com.dgsd.ksol.solpay.extensions.getRawQueryParameters
import com.dgsd.ksol.solpay.extensions.urlDecode
import com.dgsd.ksol.solpay.extensions.urlEncode
import com.dgsd.ksol.solpay.model.SolPayParsingException
import com.dgsd.ksol.solpay.model.SolPayTransactionRequest

internal object SolPayTransactionRequestFactory {

  fun createUrl(request: SolPayTransactionRequest): String {
    return buildString {
      append(SolPayConstants.SCHEME_SOLANA)
      append(':')

      if (request.link.getRawQueryParameters().isEmpty()) {
        // No need to URL encode
        append(request.link)
      } else {
        append(request.link.urlEncode())
      }
    }
  }

  fun createRequest(url: String): SolPayTransactionRequest {
    return runCatching {
      createRequestInternal(url)
    }.getOrElse {
      throw SolPayParsingException(url, it)
    }
  }

  private fun createRequestInternal(url: String): SolPayTransactionRequest {
    validateUrlLength(url)
    validateSolanaScheme(url)

    val path = url.getPathPortion()

    val link = path.urlDecode()

    if (!link.startsWith("https://")) {
      throw SolPayParsingException(url, "Not an absolute https url")
    }

    return SolPayTransactionRequest(link)
  }
}