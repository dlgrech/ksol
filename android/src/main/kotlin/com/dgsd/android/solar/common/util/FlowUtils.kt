package com.dgsd.android.solar.common.util

import com.dgsd.android.solar.common.model.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

/**
 * Converts a `suspend` method into a `Flow<Resource>`
 */
fun <T> execute(
    action: suspend () -> T,
): Flow<Resource<T>> = flow {
    emit(Resource.Loading())
    emit(Resource.Success(action.invoke()))
}.catch {
    emit(Resource.Error(it))
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