package com.dgsd.android.solar.common.util

import com.dgsd.android.solar.common.model.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Converts a `suspend` method into a `Flow<Resource>`
 */
fun <T> CoroutineScope.asResourceFlow(
    action: suspend () -> T,
    context: CoroutineContext = coroutineContext
): Flow<Resource<T>> {
    val result = MutableStateFlow<Resource<T>>(Resource.Loading())
    launch(context) {
        runCatching {
            action.invoke()
        }.onSuccess {
            result.value = Resource.Success(it)
        }.onFailure {
            result.value = Resource.Error(it)
        }
    }

    return result
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