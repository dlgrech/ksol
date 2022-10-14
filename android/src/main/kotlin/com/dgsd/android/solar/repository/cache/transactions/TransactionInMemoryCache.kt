package com.dgsd.android.solar.repository.cache.transactions

import com.dgsd.android.solar.cache.InMemoryCache
import com.dgsd.ksol.core.model.Transaction
import com.dgsd.ksol.core.model.TransactionSignature

/**
 * Cache for holding single transactions in memory
 */
class TransactionInMemoryCache : InMemoryCache<TransactionSignature, Transaction>(),
  TransactionCache