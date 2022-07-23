package com.dgsd.android.solar.extensions

import android.widget.LinearLayout
import androidx.core.view.children

/**
 * Ensures that this [LinearLayout] instance has exactly [desiredChildCount] child views.
 *
 * This method will either remove excess views, or add them (according to [addViewAction]) as
 * required
 *
 * @param desiredChildCount the number of child views we want to ensure this [LinearLayout] has
 * @param addViewAction lambda that is invoked when a single, new view needs to be added to the
 * container.
 */
fun LinearLayout.ensureViewCount(desiredChildCount: Int, addViewAction: () -> Unit) {
  val existingViews = children.toList()
  val viewsToAdd = (desiredChildCount - existingViews.size).coerceAtLeast(0)
  val viewsToRemove = (existingViews.size - desiredChildCount).coerceAtLeast(0)

  // Remove any extra views
  removeViews(0, viewsToRemove)

  repeat(viewsToAdd) {
    addViewAction.invoke()
  }
}