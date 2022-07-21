package com.dgsd.android.solar.widget.keyboard

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.dgsd.android.solar.R
import com.dgsd.android.solar.extensions.performHapticFeedback

class NumericKeyboardView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

  private var onNumericKeyPressedCallback: OnNumericKeyPressedListener? = null
  private var onBackspaceKeyPressedCallback: OnBackspaceKeyPressedListener? = null
  private var onBackspaceKeyLongPressedCallback: OnBackspaceKeyPressedListener? = null

  init {
    LayoutInflater.from(context).inflate(R.layout.view_numeric_keyboard, this, true)

    clipChildren = false
    clipToPadding = false

    arrayOf<TextView>(
      findViewById(R.id.key_0),
      findViewById(R.id.key_1),
      findViewById(R.id.key_2),
      findViewById(R.id.key_3),
      findViewById(R.id.key_4),
      findViewById(R.id.key_5),
      findViewById(R.id.key_6),
      findViewById(R.id.key_7),
      findViewById(R.id.key_8),
      findViewById(R.id.key_9),
    ).forEachIndexed { index, keyView ->
      keyView.setOnClickListener {
        keyView.performHapticFeedback()
        onNumericKeyPressedCallback?.invoke(index)
      }
    }

    findViewById<View>(R.id.backspace).apply {
      val backspaceKeyView = this
      setOnClickListener {
        backspaceKeyView.performHapticFeedback()
        onBackspaceKeyPressedCallback?.invoke()
      }
      setOnLongClickListener {
        backspaceKeyView.performHapticFeedback()
        onBackspaceKeyLongPressedCallback?.invoke()
        true
      }
    }
  }

  fun setOnNumericKeyPressed(callback: OnNumericKeyPressedListener) {
    onNumericKeyPressedCallback = callback
  }

  fun setOnBackspaceKeyPressed(callback: OnBackspaceKeyPressedListener) {
    onBackspaceKeyPressedCallback = callback
  }

  fun setOnBackspaceKeyLongPressed(callback: OnBackspaceKeyPressedListener) {
    onBackspaceKeyLongPressedCallback = callback
  }
}