package com.dgsd.ksol.solpay.factory

import com.dgsd.ksol.model.LAMPORTS_IN_SOL
import com.dgsd.ksol.model.PublicKey
import com.dgsd.ksol.solpay.extensions.getPathName
import com.dgsd.ksol.solpay.extensions.getRawQueryParameters
import com.dgsd.ksol.solpay.extensions.urlDecode
import com.dgsd.ksol.solpay.extensions.urlEncode
import com.dgsd.ksol.solpay.model.SolPayParsingException
import com.dgsd.ksol.solpay.model.SolPayTransferRequest
import java.math.BigDecimal
import java.net.URI

object SolPayTransferRequestFactory {

  private const val QUERY_PARAM_SPL_TOKEN = "spl-token"
  private const val QUERY_PARAM_AMOUNT = "amount"
  private const val QUERY_PARAM_REFERENCE = "reference"
  private const val QUERY_PARAM_LABEL = "label"
  private const val QUERY_PARAM_MESSAGE = "message"
  private const val QUERY_PARAM_MEMO = "memo"

  fun createUrl(request: SolPayTransferRequest): String {
    return buildString {
      append(SolPayConstants.SCHEME_SOLANA)
      append(':')
      append(request.recipient.toBase58String())

      val queryParams = buildList {
        if (request.amount != null) {
          add(QUERY_PARAM_AMOUNT + "=" + request.amount.toPlainString())
        }

        if (request.splTokenMintAccount != null) {
          add(QUERY_PARAM_SPL_TOKEN + "=" + request.splTokenMintAccount.toBase58String())
        }

        request.references.forEach { reference ->
          add(QUERY_PARAM_REFERENCE + "=" + reference.toBase58String())
        }

        if (request.label != null) {
          add(QUERY_PARAM_LABEL + "=" + request.label.urlEncode())
        }

        if (request.message != null) {
          add(QUERY_PARAM_MESSAGE + "=" + request.message.urlEncode())
        }

        if (request.memo != null) {
          add(QUERY_PARAM_MEMO + "=" + request.memo.urlEncode())
        }
      }.joinToString("&")

      if (queryParams.isNotEmpty()) {
        append('?')
        append(queryParams)
      }
    }
  }

  fun createRequest(url: String): SolPayTransferRequest {
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
      splTokenInput = queryParams[QUERY_PARAM_SPL_TOKEN]?.singleOrNull(),
      amountInput = queryParams[QUERY_PARAM_AMOUNT]?.singleOrNull(),
      referencesInput = queryParams[QUERY_PARAM_REFERENCE],
      labelInput = queryParams[QUERY_PARAM_LABEL]?.singleOrNull(),
      messageInput = queryParams[QUERY_PARAM_MESSAGE]?.singleOrNull(),
      memoInput = queryParams[QUERY_PARAM_MEMO]?.singleOrNull(),
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
      check(amount > BigDecimal.ZERO) {
        "Cannot have a negative amount"
      }

      check(LAMPORTS_IN_SOL * amount >= BigDecimal.ONE) {
        "Amount has too many decimals"
      }
    }

    return SolPayTransferRequest(
      recipient = PublicKey.fromBase58(recipientInput.orEmpty()),
      amount = amount,
      splTokenMintAccount = splTokenInput?.let { PublicKey.fromBase58(it) },
      references = referencesInput.orEmpty().map(PublicKey::fromBase58),
      label = labelInput?.urlDecode(),
      message = messageInput?.urlDecode(),
      memo = memoInput?.urlDecode(),
    )
  }
}