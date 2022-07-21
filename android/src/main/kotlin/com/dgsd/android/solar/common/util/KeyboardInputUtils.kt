package com.dgsd.android.solar.common.util

import android.text.TextUtils
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.dgsd.android.solar.widget.keyboard.NumericKeyboardView

object KeyboardInputUtils {

  fun setup(
    keyboardView: NumericKeyboardView,
    display: TextView,
    onInputChanged: (String) -> Unit,
  ) {

    keyboardView.setOnNumericKeyPressed { key ->
      display.text = TextUtils.concat(display.text ?: "", key.toString())
    }

    keyboardView.setOnBackspaceKeyPressed {
      val currentText = display.text
      if (!currentText.isNullOrEmpty()) {
        display.text = currentText.take(currentText.length - 1)
      }
    }

    keyboardView.setOnBackspaceKeyLongPressed {
      display.text = ""
    }

    display.doAfterTextChanged {
      onInputChanged.invoke(display.text.toString())
    }
  }
}