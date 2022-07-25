package com.dgsd.android.solar.repository.cache.memory

import com.dgsd.android.solar.cache.CacheEntry
import com.dgsd.android.solar.model.LamportsWithTimestamp
import com.dgsd.android.solar.repository.cache.BalanceCache
import com.dgsd.ksol.model.PublicKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * [BalanceCache] implementation that just holds values in memory
 */
class BalanceMemoryCache : BalanceCache {

  private val accountKeyToBalance =
    mutableMapOf<PublicKey, MutableStateFlow<CacheEntry<LamportsWithTimestamp>?>>()

  override suspend fun set(key: PublicKey, value: LamportsWithTimestamp) {
    getBalanceFlow(key).value = CacheEntry.of(value)
  }

  override fun get(key: PublicKey): Flow<CacheEntry<LamportsWithTimestamp>?> {
    return getBalanceFlow(key)
  }

  private fun getBalanceFlow(
    accountKey: PublicKey
  ): MutableStateFlow<CacheEntry<LamportsWithTimestamp>?> {
    return accountKeyToBalance.getOrPut(accountKey) { MutableStateFlow(null) }
  }
}