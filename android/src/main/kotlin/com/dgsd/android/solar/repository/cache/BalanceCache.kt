package com.dgsd.android.solar.repository.cache

import com.dgsd.android.solar.cache.Cache
import com.dgsd.android.solar.model.LamportsWithTimestamp
import com.dgsd.ksol.model.PublicKey

/**
 * Cache for holding the balance of accounts
 */
interface BalanceCache : Cache<PublicKey, LamportsWithTimestamp>