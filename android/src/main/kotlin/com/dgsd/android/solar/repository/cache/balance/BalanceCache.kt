package com.dgsd.android.solar.repository.cache.balance

import com.dgsd.android.solar.cache.Cache
import com.dgsd.android.solar.model.LamportsWithTimestamp
import com.dgsd.ksol.core.model.PublicKey

/**
 * Cache for holding the balance of accounts
 */
interface BalanceCache : Cache<PublicKey, LamportsWithTimestamp>