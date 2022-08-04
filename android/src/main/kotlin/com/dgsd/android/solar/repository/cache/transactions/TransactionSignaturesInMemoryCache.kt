package com.dgsd.android.solar.repository.cache.transactions

import com.dgsd.android.solar.cache.InMemoryCache
import com.dgsd.ksol.model.TransactionSignatureInfo

class TransactionSignaturesInMemoryCache :
  InMemoryCache<TransactionSignaturesCache.TransactionSignaturesCacheKey, List<TransactionSignatureInfo>>(),
  TransactionSignaturesCache