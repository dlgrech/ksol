package com.dgsd.android.solar.widget.edittext

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doAfterTextChanged

class EditableTextView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

  init {
    isCursorVisible = true
    isFocusable = true
    isFocusableInTouchMode = true
    showSoftInputOnFocus = false
    setTextIsSelectable(false)

    doAfterTextChanged {
      setSelection(text?.length ?: 0)
    }
  }

  override fun onTouchEvent(event: MotionEvent?): Boolean {
    return false
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
    return if (keyCode == KeyEvent.KEYCODE_BACK) {
      super.onKeyDown(keyCode, event)
    } else {
      true
    }
  }

  override fun onCheckIsTextEditor(): Boolean {
    return true
  }

  override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
    outAttrs.imeOptions = EditorInfo.IME_NULL
    outAttrs.inputType = EditorInfo.TYPE_NULL
    return null
  }
}