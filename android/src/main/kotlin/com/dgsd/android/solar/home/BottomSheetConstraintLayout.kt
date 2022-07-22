package com.dgsd.android.solar.home

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.dgsd.android.solar.R
import com.dgsd.android.solar.extensions.dpToPx
import com.dgsd.android.solar.extensions.getColorAttr
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel

class BottomSheetConstraintLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

  init {
    val model = ShapeAppearanceModel()
      .toBuilder()
      .setTopLeftCorner(CornerFamily.ROUNDED, 50f)
      .setTopRightCorner(CornerFamily.ROUNDED, 50f)
      .build()

    background = MaterialShapeDrawable(model).apply {
      fillColor = ColorStateList.valueOf(context.getColorAttr(R.attr.colorSurfaceVariant))
    }
    elevation = context.dpToPx(16)
  }
}