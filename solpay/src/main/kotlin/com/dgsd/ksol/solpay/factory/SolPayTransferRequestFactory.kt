package com.dgsd.ksol.solpay.factory

import com.dgsd.ksol.model.LAMPORTS_IN_SOL
import com.dgsd.ksol.model.PublicKey
import com.dgsd.ksol.solpay.extensions.getPathName
import com.dgsd.ksol.solpay.extensions.getRawQueryParameters
import com.dgsd.ksol.solpay.model.SolPayParsingException
import com.dgsd.ksol.solpay.model.SolPayTransferRequest
import java.math.BigDecimal
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object SolPayTransferRequestFactory {

  fun create(url: String): SolPayTransferRequest {
    return runCatching {
      createInternal(url)
    }.getOrElse {
      throw SolPayParsingException(url, it)
    }
  }

  private fun createInternal(url: String): SolPayTransferRequest {
    if (url.length > SolPayConstants.MAX_URL_LENGTH) {
      throw SolPayParsingException(url, "URL too long")
    }

    val uri = URI(url)
    if (!url.startsWith(SolPayConstants.SCHEME_SOLANA + ":")) {
      throw SolPayParsingException(url, "Invalid scheme")
    }

    val queryParams = uri.getRawQueryParameters()

    return createFromParts(
      recipientInput = uri.getPathName(),
      splTokenInput = queryParams["spl-token"]?.singleOrNull(),
      amountInput = queryParams["amount"]?.singleOrNull(),
      referencesInput = queryParams["reference"],
      labelInput = queryParams["label"]?.singleOrNull(),
      messageInput = queryParams["message"]?.singleOrNull(),
      memoInput = queryParams["memo"]?.singleOrNull(),
    )
  }

  private fun createFromParts(
    recipientInput: String?,
    splTokenInput: String?,
    amountInput: String?,
    referencesInput: List<String>?,
    labelInput: String?,
    messageInput: String?,
    memoInput: String?
  ): SolPayTransferRequest {
    if (recipientInput.isNullOrEmpty()) {
      error("Invalid recipient")
    }

    val amount = amountInput?.let(::BigDecimal)
    if (amount != null && amount != BigDecimal.ZERO) {
      check(LAMPORTS_IN_SOL * amount >= BigDecimal.ONE) {
        "Amount has too many decimals"
      }
    }

    return SolPayTransferRequest(
      recipient = PublicKey.fromBase58(recipientInput.orEmpty()),
      amount = amount,
      splTokenMintAccount = splTokenInput?.let { PublicKey.fromBase58(it) },
      references = referencesInput.orEmpty().map(PublicKey::fromBase58),
      label = labelInput?.let { URLDecoder.decode(it, StandardCharsets.UTF_8) },
      message = messageInput?.let { URLDecoder.decode(it, StandardCharsets.UTF_8) },
      memo = memoInput?.let { URLDecoder.decode(it, StandardCharsets.UTF_8) }
    )
  }
}