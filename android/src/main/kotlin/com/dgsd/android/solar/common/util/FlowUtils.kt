package com.dgsd.android.solar.common.util

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

fun <T, R> Flow<Resource<T>>.flatMapSuccess(
    action: suspend (T) -> Flow<Resource<R>>
): Flow<Resource<R>> {
    return this.flatMapLatest { resource ->
        when (resource) {
            is Resource.Error -> flowOf(Resource.Error(resource.error))
            is Resource.Loading -> flowOf(Resource.Loading(data = null))
            is Resource.Success -> action.invoke(resource.data)
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