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

  fun formatDateAndTimeLong(
    context: Context,
    dateTime: OffsetDateTime
  ): CharSequence {
    return DateUtils.formatDateTime(
      context,
      TimeUnit.SECONDS.toMillis(dateTime.toEpochSecond()),
      DateUtils.FORMAT_SHOW_DATE or
        DateUtils.FORMAT_SHOW_YEAR or
        DateUtils.FORMAT_SHOW_WEEKDAY or
        DateUtils.FORMAT_SHOW_TIME
    )
  }
}