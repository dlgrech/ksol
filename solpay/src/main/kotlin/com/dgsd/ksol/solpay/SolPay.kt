package com.dgsd.ksol.solpay

import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.model.Cluster
import com.dgsd.ksol.model.PublicKey
import com.dgsd.ksol.solpay.model.*
import okhttp3.OkHttpClient

/**
 * Creates a default implementation of [SolPay] using the given [Cluster]
 */
fun SolPay(
  okHttpClient: OkHttpClient = OkHttpClient(),
  solanaApi: SolanaApi,
): SolPay {
  return SolPayImpl(okHttpClient, solanaApi)
}

/**
 * API for performing SolPay-specific operations, as defined in the
 * [SolPay spec](https://github.com/solana-labs/solana-pay/blob/master/SPEC.md)
 */
interface SolPay {

  /**
   * Construct a SolPay-compatible URL from the given transfer request
   *
   * This URL can be used to share with other SolPay compatible systems
   */
  fun createUrl(request: SolPayRequest): String

  /**
   * Parses the given URL and return a SolPay request object, if applicable.
   *
   * @see [SolPayRequest]
   */
  fun parseUrl(url: String): SolPayRequest?

  /**
   * Fetch the transaction request details for the SolPay transaction request
   *
   * @see https://github.com/solana-labs/solana-pay/blob/master/SPEC.md#get-request
   */
  suspend fun getDetails(request: SolPayTransactionRequest): SolPayTransactionRequestDetails

  /**
   * Fetches the actual transaction details to be performed as part of a SolPay transaction request
   *
   * @see https://github.com/solana-labs/solana-pay/blob/master/SPEC.md#post-request
   */
  suspend fun getTransaction(
    signingWalletAddress: PublicKey,
    request: SolPayTransactionRequest
  ): SolPayTransactionInfo
}