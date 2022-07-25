package com.dgsd.android.solar.cache

/**
 * Different strategies for how we should fetch values from cache
 */
enum class CacheStrategy {

  /**
   * Fetch a value only from local cache
   */
  CACHE_ONLY,

  /**
   * Fetch a value, only from a remote network source, ignoring any cache
   */
  NETWORK_ONLY,

  /**
   * Fetch a value from cache if present. If missing, fetch from network
   */
  CACHE_IF_PRESENT,

  /**
   * Fetch a value from cache, and also refresh it from a network source
   */
  CACHE_AND_NETWORK
}