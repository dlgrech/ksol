package com.dgsd.android.solar.common.ui

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.TextUtils
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.text.bold
import androidx.core.text.color
import com.dgsd.android.solar.extensions.getColorAttr

object RichTextFormatter {

  fun expandTemplate(
    context: Context,
    @StringRes templateRes: Int,
    vararg arguments: CharSequence
  ): CharSequence {
    return TextUtils.expandTemplate(context.getString(templateRes), *arguments)
  }

  fun coloredText(@ColorInt color: Int, text: CharSequence): CharSequence {
    return SpannableStringBuilder().color(color) { append(text) }
  }

  fun coloredTextAttr(context: Context, @AttrRes attrId: Int, text: CharSequence): CharSequence {
    val color = context.getColorAttr(attrId)
    return coloredText(color, text)
  }

  fun bold(context: Context, @StringRes textRes: Int): CharSequence {
    return bold(context.getString(textRes))
  }

  fun bold(text: CharSequence): CharSequence {
    return SpannableStringBuilder().bold { append(text) }
  }
}