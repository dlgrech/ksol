package com.dgsd.android.solar.repository.cache.balance

import com.dgsd.android.solar.cache.InMemoryCache
import com.dgsd.android.solar.model.LamportsWithTimestamp
import com.dgsd.ksol.core.model.PublicKey

/**
 * [BalanceCache] implementation that just holds values in memory
 */
class BalanceMemoryCache : InMemoryCache<PublicKey, LamportsWithTimestamp>(), BalanceCache