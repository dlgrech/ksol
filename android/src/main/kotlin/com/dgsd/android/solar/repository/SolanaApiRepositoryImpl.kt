package com.dgsd.android.solar.repository

import com.dgsd.android.solar.cache.CacheStrategy
import com.dgsd.android.solar.common.model.Resource
import com.dgsd.android.solar.common.util.executeWithCache
import com.dgsd.android.solar.common.util.flatMapSuccess
import com.dgsd.android.solar.common.util.resourceFlowOf
import com.dgsd.android.solar.model.LamportsWithTimestamp
import com.dgsd.android.solar.model.TransactionOrSignature
import com.dgsd.android.solar.repository.cache.balance.BalanceCache
import com.dgsd.android.solar.repository.cache.balance.TransactionCache
import com.dgsd.android.solar.repository.cache.balance.TransactionSignaturesCache
import com.dgsd.android.solar.repository.cache.balance.TransactionSignaturesCache.TransactionSignaturesCacheKey
import com.dgsd.android.solar.session.model.WalletSession
import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.model.Transaction
import com.dgsd.ksol.model.TransactionSignature
import com.dgsd.ksol.model.TransactionSignatureInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime

internal class SolanaApiRepositoryImpl(
  private val session: WalletSession,
  private val solanaApi: SolanaApi,
  private val balanceCache: BalanceCache,
  private val transactionCache: TransactionCache,
  private val transactionSignaturesCache: TransactionSignaturesCache,
) : SolanaApiRepository {

  override fun getBalance(
    cacheStrategy: CacheStrategy
  ): Flow<Resource<LamportsWithTimestamp>> {
    val accountKey = session.publicKey
    return executeWithCache(
      cacheKey = accountKey,
      cacheStrategy = cacheStrategy,
      cache = balanceCache,
      networkFlowProvider = {
        resourceFlowOf {
          LamportsWithTimestamp(solanaApi.getBalance(accountKey), OffsetDateTime.now())
        }
      }
    )
  }

  override fun getTransactions(
    cacheStrategy: CacheStrategy,
    limit: Int
  ): Flow<Resource<List<Resource<TransactionOrSignature>>>> {
    return getTransactionSignatures(
      cacheStrategy, limit
    ).flatMapSuccess { signatureList ->
      val transactionsFlow = signatureList.map { signatureInfo ->
        getTransaction(cacheStrategy, signatureInfo.signature).map { transactionResource ->
          when (transactionResource) {
            is Resource.Error -> {
              Resource.Error(
                error = transactionResource.error,
                data = TransactionOrSignature(signatureInfo.signature)
              )
            }

            is Resource.Loading -> {
              Resource.Loading(
                data = transactionResource.data?.let { TransactionOrSignature(it) }
                  ?: TransactionOrSignature(signatureInfo.signature)
              )
            }

            is Resource.Success -> {
              Resource.Success(TransactionOrSignature(transactionResource.data))
            }
          }
        }
      }

      combine(transactionsFlow) { transactionsArray ->
        transactionsArray.toList()
      }.map { transactionList ->
        Resource.Success(transactionList)
      }
    }
  }

  private fun getTransactionSignatures(
    cacheStrategy: CacheStrategy,
    limit: Int
  ): Flow<Resource<List<TransactionSignatureInfo>>> {
    return executeWithCache(
      cacheKey = TransactionSignaturesCacheKey(session.publicKey, limit),
      cacheStrategy = cacheStrategy,
      cache = transactionSignaturesCache,
      networkFlowProvider = {
        resourceFlowOf {
          solanaApi.getSignaturesForAddress(
            accountKey = session.publicKey,
            limit = limit
          )
        }
      }
    )
  }

  private fun getTransaction(
    cacheStrategy: CacheStrategy,
    signature: TransactionSignature,
  ): Flow<Resource<Transaction>> {
    return executeWithCache(
      cacheKey = signature,
      cacheStrategy = cacheStrategy,
      cache = transactionCache,
      networkFlowProvider = {
        resourceFlowOf {
          checkNotNull(solanaApi.getTransaction(signature))
        }
      }
    )
  }
}