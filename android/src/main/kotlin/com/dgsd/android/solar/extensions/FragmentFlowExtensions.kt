package com.dgsd.android.solar.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun <T> Fragment.onEach(flow: Flow<T>, action: (T) -> Unit) {
  flow.onEach(action).launchIn(viewLifecycleOwner.lifecycleScope)
}