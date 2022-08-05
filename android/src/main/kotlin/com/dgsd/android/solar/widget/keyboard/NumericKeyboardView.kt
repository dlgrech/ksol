package com.dgsd.android.solar.widget.keyboard

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import com.dgsd.android.solar.R
import com.dgsd.android.solar.extensions.performHapticFeedback

class NumericKeyboardView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

  private var onNumericKeyPressedCallback: OnNumericKeyPressedListener? = null
  private var onBackspaceKeyPressedCallback: OnBackspaceKeyPressedListener? = null
  private var onBackspaceKeyLongPressedCallback: OnBackspaceKeyPressedListener? = null
  private var onCustomKeyPressedListener: OnCustomKeyPressedListener? = null

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

    findViewById<ImageView>(R.id.custom_action).setOnClickListener {
      onCustomKeyPressedListener?.invoke()
    }
  }

  fun setCustomActionImage(drawable: Drawable?) {
    val view = findViewById<ImageView>(R.id.custom_action)
    if (drawable == null) {
      view.isInvisible = true
    } else {
      view.isInvisible = false
      view.setImageDrawable(drawable)
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

  fun setOnCustomActionPressed(callback: OnCustomKeyPressedListener) {
    onCustomKeyPressedListener = callback
  }
}