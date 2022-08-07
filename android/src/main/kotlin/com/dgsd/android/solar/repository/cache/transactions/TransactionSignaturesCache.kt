package com.dgsd.android.solar.repository.cache.transactions

import com.dgsd.android.solar.cache.Cache
import com.dgsd.android.solar.repository.cache.transactions.TransactionSignaturesCache.TransactionSignaturesCacheKey
import com.dgsd.ksol.model.PublicKey
import com.dgsd.ksol.model.TransactionSignature
import com.dgsd.ksol.model.TransactionSignatureInfo

interface TransactionSignaturesCache :
  Cache<TransactionSignaturesCacheKey, List<TransactionSignatureInfo>> {

  data class TransactionSignaturesCacheKey(
    val accountKey: PublicKey,
    val beforeSignature: TransactionSignature?
  )
}