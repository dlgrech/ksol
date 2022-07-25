package com.dgsd.android.solar.common.util

import com.dgsd.android.solar.cache.Cache
import com.dgsd.android.solar.cache.CacheStrategy
import com.dgsd.android.solar.common.model.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

/**
 * Converts a `suspend` method into a `Flow<Resource>`
 */
fun <T> resourceFlowOf(
  context: CoroutineContext = Dispatchers.IO,
  action: suspend () -> T,
): Flow<Resource<T>> {
  return flow<Resource<T>> {
    emit(Resource.Loading())
    runCatching {
      action.invoke()
    }.onSuccess {
      emit(Resource.Success(it))
    }.onFailure {
      emit(Resource.Error(it))
    }
  }.flowOn(context)
}

fun <T> Flow<Resource<T>>.onEachSuccess(
  action: suspend (T) -> Unit
): Flow<Resource<T>> {
  return onEach { resource ->
    if (resource is Resource.Success) {
      action.invoke(resource.data)
    }
  }
}

/**
 * Emits [true] if any of the given flows have a value of [true], [false] otherwise
 */
fun anyTrue(vararg flows: Flow<Boolean>): Flow<Boolean> {
  return combine(
    flows = flows,
    transform = { values ->
      values.any { it }
    }
  )
}

fun <K, V> Cache<K, V>.asResourceFlow(key: K): Flow<Resource<V>> {
  return get(key).flatMapLatest { cacheEntry ->
    if (cacheEntry == null) {
      flowOf(Resource.Error(IllegalStateException("Value missing from cache")))
    } else {
      flowOf(Resource.Success(cacheEntry.cacheData))
    }
  }.onStart { emit(Resource.Loading(null)) }
}

fun <K, V> Flow<Resource<V>>.withCache(
  key: K,
  cache: Cache<K, V>
): Flow<Resource<V>> {
    return onEachSuccess { cache.set(key, it) }
}

fun <K, V> executeWithCache(
    cacheKey: K,
    cacheStrategy: CacheStrategy,
    cache: Cache<K, V>,
    networkFlowProvider: () -> Flow<Resource<V>>
): Flow<Resource<V>> {
    return when (cacheStrategy) {
        CacheStrategy.CACHE_ONLY -> {
            cache.asResourceFlow(cacheKey)
        }

        CacheStrategy.NETWORK_ONLY -> {
            networkFlowProvider.invoke().withCache(cacheKey, cache)
        }

        CacheStrategy.CACHE_IF_PRESENT -> {
            cache.get(cacheKey).flatMapLatest { cacheEntry ->
                if (cacheEntry != null) {
                    flowOf(Resource.Success(cacheEntry.cacheData))
                } else {
                    executeWithCache(
                        cacheKey,
                        CacheStrategy.NETWORK_ONLY,
                        cache,
                        networkFlowProvider
                    )
                }
            }.onStart { emit(Resource.Loading(null)) }
        }

        CacheStrategy.CACHE_AND_NETWORK -> {
            val networkFlow = executeWithCache(
                cacheKey,
                CacheStrategy.NETWORK_ONLY,
                cache,
                networkFlowProvider
            )

            val cacheFlow = cache
                .get(cacheKey)
                .mapNotNull { cacheEntry ->
                    if (cacheEntry == null) {
                        null
                    } else {
                        Resource.Success(cacheEntry.cacheData) as Resource<V>
                    }
                }.onStart { emit(Resource.Loading(null)) }

            return combine(cacheFlow, networkFlow) { cacheResource, networkResource ->
                when (networkResource) {
                    is Resource.Error<*> -> {
                        if (networkResource.data != null) {
                            networkResource
                        } else {
                            Resource.Error(networkResource.error, cacheResource.dataOrNull())
                        }
                    }
                    is Resource.Success<*> -> {
                        cacheResource
                    }
                    is Resource.Loading<*> -> {
                        // If we have no data with the Loading state, use our cache
                        if (networkResource.data != null) {
                            networkResource
                        } else {
                            cacheResource
                        }
                    }
                }
            }
        }
    }
}