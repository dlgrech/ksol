package com.dgsd.android.solar.repository

import com.dgsd.android.solar.cache.CacheStrategy
import com.dgsd.android.solar.common.model.Resource
import com.dgsd.android.solar.common.util.executeWithCache
import com.dgsd.android.solar.common.util.resourceFlowOf
import com.dgsd.android.solar.model.LamportsWithTimestamp
import com.dgsd.android.solar.model.TransactionInfo
import com.dgsd.android.solar.repository.cache.BalanceCache
import com.dgsd.android.solar.session.model.WalletSession
import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.model.TransactionSignature
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

internal class SolanaApiRepositoryImpl(
  private val session: WalletSession,
  private val solanaApi: SolanaApi,
  private val balanceCache: BalanceCache,
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
  ): Flow<Resource<List<TransactionInfo>>> {
    return resourceFlowOf {
      solanaApi.getSignaturesForAddress(
        accountKey = session.publicKey,
        limit = limit
      ).map {
        getTransaction(it.signature)
      }
    }
  }

  private suspend fun getTransaction(signature: TransactionSignature): TransactionInfo {
    val transaction = solanaApi.getTransaction(signature)
    return if (transaction == null) {
      TransactionInfo.UnknownTransaction(signature)
    } else {
      TransactionInfo.FullTransaction(transaction)
    }
  }
}