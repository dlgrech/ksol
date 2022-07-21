package com.dgsd.android.solar.common.util

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class SwallowBackpressLifecycleObserver private constructor(
  val fragment: Fragment
) : DefaultLifecycleObserver {

  private val swallowBackPressHandler = object : OnBackPressedCallback(true) {
    override fun handleOnBackPressed() = Unit
  }

  override fun onStart(owner: LifecycleOwner) {
    fragment.requireActivity().onBackPressedDispatcher.addCallback(swallowBackPressHandler)
  }

  override fun onStop(owner: LifecycleOwner) {
    swallowBackPressHandler.remove()
  }

  companion object {

    fun attach(fragment: Fragment) {
      val observer = SwallowBackpressLifecycleObserver(fragment)
      fragment.lifecycle.addObserver(observer)
    }
  }
}