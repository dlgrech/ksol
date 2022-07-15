package com.dgsd.android.solar.extensions

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes

private fun Context.getAttr(@AttrRes id: Int): TypedValue {
  val resolvedAttr = TypedValue()
  theme.resolveAttribute(id, resolvedAttr, true)
  return resolvedAttr
}

fun Context.getColorAttr(@AttrRes id: Int): Int {
  val colorAttr = getAttr(id)
  return if (colorAttr.resourceId == 0) {
    if (colorAttr.data == TypedValue.DATA_NULL_UNDEFINED || colorAttr.data == TypedValue.DATA_NULL_EMPTY) {
      error("Color for theme attr '${resources.getResourceName(id)}' not found")
    } else {
      colorAttr.data
    }
  } else {
    getColor(colorAttr.resourceId)
  }
}