package com.dgsd.android.solar.common.ui

import android.content.Context
import android.text.format.DateUtils
import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit

object DateTimeFormatter {

  fun formatRelativeDateAndTime(
    context: Context,
    dateTime: OffsetDateTime
  ): CharSequence {
    return DateUtils.getRelativeDateTimeString(
      context,
      TimeUnit.SECONDS.toMillis(dateTime.toEpochSecond()),
      DateUtils.DAY_IN_MILLIS,
      DateUtils.DAY_IN_MILLIS,
      DateUtils.FORMAT_ABBREV_ALL
    )
  }
}