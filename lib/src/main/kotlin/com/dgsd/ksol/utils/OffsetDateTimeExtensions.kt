package com.dgsd.ksol.utils

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * Interprets this [Long] as an epoch seconds value and converts it to a [OffsetDateTime]
 */
fun Long.toOffsetDateTime(): OffsetDateTime {
    val offset = ZoneOffset.UTC
    val localDateTime = LocalDateTime.ofEpochSecond(this, 0, offset)

    return OffsetDateTime.of(localDateTime, offset)
}