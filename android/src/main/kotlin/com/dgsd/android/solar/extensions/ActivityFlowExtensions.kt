package com.dgsd.android.solar.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun <T> AppCompatActivity.onEach(flow: Flow<T>, action: suspend (T) -> Unit) {
  flow.onEach(action).launchIn(lifecycleScope)
}