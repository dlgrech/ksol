package com.dgsd.android.solar.repository.cache.balance

import com.dgsd.android.solar.cache.Cache
import com.dgsd.android.solar.repository.cache.balance.TransactionSignaturesCache.TransactionSignaturesCacheKey
import com.dgsd.ksol.model.PublicKey
import com.dgsd.ksol.model.TransactionSignature
import com.dgsd.ksol.model.TransactionSignatureInfo

interface TransactionSignaturesCache :
  Cache<TransactionSignaturesCacheKey, List<TransactionSignatureInfo>> {

  data class TransactionSignaturesCacheKey constructor(
    val account: PublicKey,
    val limit: Int,
    val beforeSignature: TransactionSignature?
  )
}