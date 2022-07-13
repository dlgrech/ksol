package com.dgsd.android.solar.common.util

import com.dgsd.android.solar.common.model.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest

/**
 * Helper class for mapping a [Flow<Resource>] to something more useful at the UI level
 */
class ResourceFlowConsumer<T>(
    scope: CoroutineScope,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<Throwable?>(null)
    val error = _error.asStateFlow()

    private val _data = MutableStateFlow<T?>(null)
    val data = _data.asStateFlow()

    private var existingJob: Job? = null

    val coroutineScope = (scope + dispatcher)

    fun collectFlow(flow: Flow<Resource<T>>) {
        existingJob?.cancel()
        existingJob = coroutineScope.launch {
            flow.collectLatest { resource ->
                _isLoading.value = resource is Resource.Loading
                _data.value = resource.dataOrNull()
                _error.value = (resource as? Resource.Error)?.error
            }
        }
    }

}