package com.dgsd.android.solar.repository.cache.balance

import com.dgsd.android.solar.cache.InMemoryCache
import com.dgsd.ksol.model.TransactionSignatureInfo

class TransactionSignaturesInMemoryCache :
  InMemoryCache<TransactionSignaturesCache.TransactionSignaturesCacheKey, List<TransactionSignatureInfo>>(), TransactionSignaturesCache