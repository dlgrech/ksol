package com.dgsd.android.solar.common.util

import com.dgsd.android.solar.common.model.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Helper class for mapping a [Flow<Resource>] to something more useful at the UI level
 */
class ResourceFlowConsumer<T>(private val scope: CoroutineScope) {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<Throwable?>(null)
    val error = _error.asStateFlow()

    private val _data = MutableStateFlow<T?>(null)
    val data = _data.asStateFlow()

    private var existingJob: Job? = null

    fun collectFlow(action: (suspend () -> T)) {
        collectFlow(scope.asResourceFlow(action, Dispatchers.IO))
    }

    private fun collectFlow(flow: Flow<Resource<T>>) {
        existingJob?.cancel()
        existingJob = scope.launch {
            flow.collectLatest { resource ->
                _isLoading.value = resource is Resource.Loading
                _data.value = resource.dataOrNull()
                _error.value = (resource as? Resource.Error)?.error
            }
        }
    }

}