package com.dgsd.android.solar.flow

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

typealias EventFlow<T> = SharedFlow<T>
typealias MutableEventFlow<T> = MutableSharedFlow<T>

@Suppress("FunctionName")
fun <T> MutableEventFlow(): MutableEventFlow<T> {
    return MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
}

fun <T> MutableEventFlow<T>.asEventFlow(): EventFlow<T> {
    return this
}
