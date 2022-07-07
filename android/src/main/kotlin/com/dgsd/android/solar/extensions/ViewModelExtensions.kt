package com.dgsd.android.solar.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.common.model.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * Converts a `suspend` method into a `Flow<Resource>`
 */
fun <T> ViewModel.execute(
    action: suspend () -> T
): Flow<Resource<T>> {
    val result = MutableStateFlow<Resource<T>>(Resource.Loading())
    viewModelScope.launch {
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