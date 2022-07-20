package com.dgsd.ksol.flow

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * `MutableSharedFlow` instance that can be used for emitting values as events.
 *
 * That is, they should only be handled [exactly once] by what ever subscriber is active at the time of emission.
 */
internal typealias MutableEventFlow<T> = MutableSharedFlow<T>

/**
 * Factory for creating [MutableEventFlow] instances
 */
internal fun <T> MutableEventFlow(): MutableEventFlow<T> {
    return MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
}