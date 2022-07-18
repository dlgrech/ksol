package com.dgsd.android.solar.common.shimmer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IntDef
import com.dgsd.android.solar.R
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable

@Retention(AnnotationRetention.SOURCE)
@IntDef(ShapeType.RoundedRect, ShapeType.Circle)
private annotation class ShapeType {
  companion object {
    const val RoundedRect = 0
    const val Circle = 1
  }
}

/**
 * Uses a [ShimmerDrawable] to show a shimering loading state.
 *
 * This is handy compared to `ShimmerFrameLayout` as we dont need to wrap child views in a
 * parent layout
 */
@SuppressLint("CustomViewStyleable")
class ShimmerView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
) : View(context, attrs) {

  private val shimmerDrawable = ShimmerDrawable().apply {
    callback = this@ShimmerView
  }

  init {
    setWillNotDraw(false)
    shimmerDrawable.setShimmer(
      Shimmer.AlphaHighlightBuilder()
        .consumeAttributes(context, attrs)
        .setDuration(1500)
        .setHighlightAlpha(0.5f)
        .build()
    )
    setLayerType(LAYER_TYPE_HARDWARE, Paint())

    val ta = context.obtainStyledAttributes(attrs, R.styleable.ShimmerView)
    try {
      val shape = ta.getInt(R.styleable.ShimmerView_shape, ShapeType.RoundedRect)
      setBackgroundResource(
        when (shape) {
          ShapeType.Circle -> R.drawable.shimmer_background_circle
          else -> R.drawable.shimmer_background_rounded
        }
      )
    } finally {
      ta.recycle()
    }
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    val width = width
    val height = height
    shimmerDrawable.setBounds(0, 0, width, height)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    if (!shimmerDrawable.isShimmerStarted) {
      shimmerDrawable.startShimmer()
    }
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    shimmerDrawable.stopShimmer()
  }

  override fun dispatchDraw(canvas: Canvas) {
    super.dispatchDraw(canvas)
    shimmerDrawable.draw(canvas)
  }

  override fun verifyDrawable(who: Drawable): Boolean {
    return super.verifyDrawable(who) || who === shimmerDrawable
  }
}