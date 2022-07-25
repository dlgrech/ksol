package com.dgsd.android.solar.cache

import java.time.OffsetDateTime

/**
 * Represents an entry in a [Cache] instance
 */
class CacheEntry<T> private constructor(
  val cacheData: T,
  val createdAt: OffsetDateTime,
) {

  companion object {

    fun <T> of(cacheData: T): CacheEntry<T> {
      return CacheEntry(cacheData, OffsetDateTime.now())
    }
  }
}