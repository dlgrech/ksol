package com.dgsd.android.solar.repository.cache.transactions

import com.dgsd.android.solar.cache.Cache
import com.dgsd.ksol.model.Transaction
import com.dgsd.ksol.model.TransactionSignature

/**
 * Cache for holding the individual transactions
 */
interface TransactionCache : Cache<TransactionSignature, Transaction>