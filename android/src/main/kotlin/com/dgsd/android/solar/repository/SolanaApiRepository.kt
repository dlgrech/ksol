package com.dgsd.android.solar.repository

import com.dgsd.android.solar.cache.CacheStrategy
import com.dgsd.android.solar.common.model.Resource
import com.dgsd.android.solar.model.LamportsWithTimestamp
import com.dgsd.android.solar.model.TransactionInfo
import kotlinx.coroutines.flow.Flow

interface SolanaApiRepository {

    fun getBalance(
        cacheStrategy: CacheStrategy = CacheStrategy.CACHE_IF_PRESENT
    ): Flow<Resource<LamportsWithTimestamp>>

    fun getTransactions(
        cacheStrategy: CacheStrategy = CacheStrategy.CACHE_IF_PRESENT,
        limit: Int
    ): Flow<Resource<List<TransactionInfo>>>
}