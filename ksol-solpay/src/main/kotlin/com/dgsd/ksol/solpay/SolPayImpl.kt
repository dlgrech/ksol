package com.dgsd.ksol.solpay

import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.solpay.api.TransactionRequestGetDetailsResponse
import com.dgsd.ksol.solpay.api.TransactionRequestTransactionDetailsRequest
import com.dgsd.ksol.solpay.api.TransactionRequestTransactionDetailsResponse
import com.dgsd.ksol.solpay.extensions.await
import com.dgsd.ksol.solpay.factory.*
import com.dgsd.ksol.solpay.model.*
import com.squareup.moshi.Moshi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

private val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()

/**
 * Internal implementation of [SolPay] interface
 */
internal class SolPayImpl(
  private val okHttpClient: OkHttpClient,
  private val solanaApi: SolanaApi,
) : SolPay {

  private val moshiJson: Moshi = Moshi.Builder().build()

  private val transactionRequestDetailsJsonAdapter =
    moshiJson.adapter(TransactionRequestGetDetailsResponse::class.java)
  private val transactionDetailsJsonAdapter =
    moshiJson.adapter(TransactionRequestTransactionDetailsResponse::class.java)
  private val transactionDetailsRequestJsonAdapter =
    moshiJson.adapter(TransactionRequestTransactionDetailsRequest::class.java)

  override fun createUrl(request: SolPayRequest): String {
    return when (request) {
      is SolPayTransactionRequest -> SolPayTransactionRequestFactory.createUrl(request)
      is SolPayTransferRequest -> SolPayTransferRequestFactory.createUrl(request)
    }
  }

  override fun parseUrl(url: String): SolPayRequest? {
    val parsingResult =
      if (url.isTransactionRequestUrl()) {
        runCatching {
          SolPayTransactionRequestFactory.createRequest(url)
        }
      } else {
        runCatching {
          SolPayTransferRequestFactory.createRequest(url)
        }
      }

    return parsingResult.getOrNull()
  }

  override suspend fun getDetails(
    request: SolPayTransactionRequest
  ): SolPayTransactionRequestDetails {
    val httpRequest = Request.Builder()
      .url(request.link)
      .get()
      .build()
    val httpResponse = okHttpClient.newCall(httpRequest).await()
    val json = checkNotNull(httpResponse.body).string()
    val detailsResponse = checkNotNull(transactionRequestDetailsJsonAdapter.fromJson(json))

    return TransactionRequestDetailsFactory.create(detailsResponse)
  }

  override suspend fun getTransaction(
    signingWalletAddress: PublicKey,
    request: SolPayTransactionRequest
  ): SolPayTransactionInfo {
    val requestBody = TransactionRequestTransactionDetailsRequest(
      account = signingWalletAddress.toBase58String()
    )
    val httpRequest = Request.Builder()
      .url(request.link)
      .post(transactionDetailsRequestJsonAdapter.toJson(requestBody).toRequestBody(MEDIA_TYPE_JSON))
      .build()

    val httpResponse = okHttpClient.newCall(httpRequest).await()
    val json = checkNotNull(httpResponse.body).string()

    val response = checkNotNull(transactionDetailsJsonAdapter.fromJson(json))

    val transactionInfo =
      TransactionRequestTransactionInfoFactory.create(signingWalletAddress, response)

    return when {
      transactionInfo.transaction.signatures.isEmpty() -> {
        transactionInfo.withUpdatedBlockhash().withUpdatedFeePayer(signingWalletAddress)
      }
      transactionInfo.hasSingleSigner(signingWalletAddress) -> {
        // The only required signature is for this account, update the recent blockhash
        transactionInfo.withUpdatedBlockhash()
      }
      else -> transactionInfo
    }
  }

  private suspend fun SolPayTransactionInfo.withUpdatedBlockhash(): SolPayTransactionInfo {
    return copy(
      transaction = transaction.copy(
        message = transaction.message.copy(
          recentBlockhash = PublicKey.fromBase58(solanaApi.getLatestBlockhash().blockhash)
        )
      )
    )
  }

  private fun SolPayTransactionInfo.withUpdatedFeePayer(
    signingWalletAddress: PublicKey
  ): SolPayTransactionInfo {
    // Set fee payer to the signing wallet
    val newAccountKeys = transaction.message.accountKeys.map { accountKey ->
      val isFeePayer = accountKey.publicKey == signingWalletAddress
      accountKey.copy(
        isFeePayer = isFeePayer,
        isWritable = isFeePayer || accountKey.isWritable,
        isSigner = isFeePayer || accountKey.isSigner
      )
    }.sortedBy {
      if (it.isFeePayer) -1 else 0
    }

    return copy(
      transaction = transaction.copy(
        message = transaction.message.copy(accountKeys = newAccountKeys)
      )
    )
  }

  private fun SolPayTransactionInfo.hasSingleSigner(signer: PublicKey): Boolean {
    return transaction.signatures.size == 1 &&
      transaction.message.accountKeys[0].publicKey == signer
  }

  private fun String.isTransactionRequestUrl(): Boolean {
    return startsWith(SolPayConstants.SCHEME_SOLANA + ":https")
  }
}