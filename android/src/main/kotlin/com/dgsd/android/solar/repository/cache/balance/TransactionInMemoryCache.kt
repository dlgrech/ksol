package com.dgsd.android.solar.repository.cache.balance

import com.dgsd.android.solar.cache.InMemoryCache
import com.dgsd.ksol.model.Transaction
import com.dgsd.ksol.model.TransactionSignature

/**
 * Cache for holding single transactions in memory
 */
class TransactionInMemoryCache : InMemoryCache<TransactionSignature, Transaction>(), TransactionCache