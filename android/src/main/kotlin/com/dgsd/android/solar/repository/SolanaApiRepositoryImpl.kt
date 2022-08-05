package com.dgsd.android.solar.repository

import com.dgsd.android.solar.cache.CacheStrategy
import com.dgsd.android.solar.common.model.Resource
import com.dgsd.android.solar.common.util.executeWithCache
import com.dgsd.android.solar.common.util.flatMapSuccess
import com.dgsd.android.solar.common.util.resourceFlowOf
import com.dgsd.android.solar.model.LamportsWithTimestamp
import com.dgsd.android.solar.model.TransactionOrSignature
import com.dgsd.android.solar.repository.cache.balance.BalanceCache
import com.dgsd.android.solar.repository.cache.transactions.TransactionCache
import com.dgsd.android.solar.repository.cache.transactions.TransactionSignaturesCache
import com.dgsd.android.solar.repository.cache.transactions.TransactionSignaturesCache.TransactionSignaturesCacheKey
import com.dgsd.android.solar.session.model.WalletSession
import com.dgsd.ksol.LocalTransactions
import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.model.*
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
    return getBalanceOfAccount(session.publicKey)
  }

  override fun getBalanceOfAccount(
    account: PublicKey,
    cacheStrategy: CacheStrategy
  ): Flow<Resource<LamportsWithTimestamp>> {
    return executeWithCache(
      cacheKey = account,
      cacheStrategy = cacheStrategy,
      cache = balanceCache,
      networkFlowProvider = {
        resourceFlowOf {
          LamportsWithTimestamp(solanaApi.getBalance(account), OffsetDateTime.now())
        }
      }
    )
  }

  override fun getTransactions(
    cacheStrategy: CacheStrategy,
    limit: Int,
    beforeSignature: TransactionSignature?
  ): Flow<Resource<List<Resource<TransactionOrSignature>>>> {
    return getTransactionSignatures(
      cacheStrategy, limit, beforeSignature
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

  override fun getTransaction(
    cacheStrategy: CacheStrategy,
    transactionSignature: TransactionSignature,
  ): Flow<Resource<Transaction>> {
    return executeWithCache(
      cacheKey = transactionSignature,
      cacheStrategy = cacheStrategy,
      cache = transactionCache,
      networkFlowProvider = {
        resourceFlowOf {
          checkNotNull(solanaApi.getTransaction(transactionSignature))
        }
      }
    )
  }

  override fun getRecentBlockhash(): Flow<Resource<RecentBlockhashResult>> {
    return resourceFlowOf {
      solanaApi.getRecentBlockhash(Commitment.FINALIZED)
    }
  }

  override fun send(
    privateKey: PrivateKey,
    recipient: PublicKey,
    lamports: Lamports
  ): Flow<Resource<TransactionSignature>> {
    return resourceFlowOf {
      val blockhash = PublicKey.fromBase58(
        solanaApi.getRecentBlockhash(Commitment.FINALIZED).blockhash
      )

      solanaApi.sendTransaction(
        LocalTransactions.createTransferTransaction(
          sender = KeyPair(session.publicKey, privateKey),
          recipient = recipient,
          lamports = lamports,
          recentBlockhash = blockhash
        )
      )
    }
  }

  private fun getTransactionSignatures(
    cacheStrategy: CacheStrategy,
    limit: Int,
    beforeSignature: TransactionSignature?
  ): Flow<Resource<List<TransactionSignatureInfo>>> {
    return executeWithCache(
      cacheKey = TransactionSignaturesCacheKey(session.publicKey, limit, beforeSignature),
      cacheStrategy = cacheStrategy,
      cache = transactionSignaturesCache,
      networkFlowProvider = {
        resourceFlowOf {
          solanaApi.getSignaturesForAddress(
            accountKey = session.publicKey,
            before = beforeSignature,
            limit = limit
          )
        }
      }
    )
  }
}