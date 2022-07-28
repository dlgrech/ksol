package com.dgsd.android.solar.repository

import com.dgsd.android.solar.cache.CacheStrategy
import com.dgsd.android.solar.common.model.Resource
import com.dgsd.android.solar.model.LamportsWithTimestamp
import com.dgsd.android.solar.model.TransactionOrSignature
import com.dgsd.ksol.model.Transaction
import com.dgsd.ksol.model.TransactionSignature
import kotlinx.coroutines.flow.Flow

interface SolanaApiRepository {

  fun getBalance(
    cacheStrategy: CacheStrategy = CacheStrategy.CACHE_IF_PRESENT
  ): Flow<Resource<LamportsWithTimestamp>>

  fun getTransactions(
    cacheStrategy: CacheStrategy = CacheStrategy.CACHE_IF_PRESENT,
    limit: Int,
    beforeSignature: TransactionSignature? = null,
  ): Flow<Resource<List<Resource<TransactionOrSignature>>>>

  fun getTransaction(
    cacheStrategy: CacheStrategy = CacheStrategy.CACHE_IF_PRESENT,
    transactionSignature: TransactionSignature,
  ): Flow<Resource<Transaction>>
}