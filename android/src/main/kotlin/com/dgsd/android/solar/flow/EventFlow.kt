package com.dgsd.android.solar.flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

typealias EventFlow<T> = SharedFlow<T>
typealias MutableEventFlow<T> = MutableSharedFlow<T>

typealias SimpleEventFlow = EventFlow<Unit>
typealias SimpleMutableEventFlow = MutableEventFlow<Unit>

fun SimpleMutableEventFlow(): SimpleMutableEventFlow {
    return MutableEventFlow()
}

fun SimpleMutableEventFlow.call() {
    tryEmit(Unit)
}

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

fun <T> Flow<T>.asEventFlow(scope: CoroutineScope): EventFlow<T> {
    val source = this
    val event = MutableEventFlow<T>()
    scope.launch {
        source.collect {
            event.emit(it)
        }
    }
    return event
}
