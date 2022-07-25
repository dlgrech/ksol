package com.dgsd.android.solar.cache

import kotlinx.coroutines.flow.Flow

/**
 * Simple/generic cache interface, for saving and retrieving data by key
 */
interface Cache<K, V> {

  suspend fun set(key: K, value: V)

  fun get(key: K): Flow<CacheEntry<V>?>
}