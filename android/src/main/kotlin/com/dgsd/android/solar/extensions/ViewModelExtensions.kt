package com.dgsd.android.solar.extensions

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel

fun AndroidViewModel.getString(
  @StringRes resId: Int,
  vararg args: Any
): String {
  return getApplication<Application>().getString(resId, args)
}