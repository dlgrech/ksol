package com.dgsd.android.solar.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun <T> ViewModel.onEach(flow: Flow<T>, action: suspend (T) -> Unit) {
  flow.onEach(action).launchIn(viewModelScope)
}