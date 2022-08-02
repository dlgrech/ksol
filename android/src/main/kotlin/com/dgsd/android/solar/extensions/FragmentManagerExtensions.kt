package com.dgsd.android.solar.extensions

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.model.ScreenTransitionType

/**
 * Similar to [FragmentManager.findFragmentById], but ensures the fragment found is actually visible
 */
fun FragmentManager.findActiveFragmentById(id: Int): Fragment? {
  val visibleFragment = this.fragments.firstOrNull {
    it.id == id && it.isVisible
  }

  return visibleFragment ?: this.fragments.firstOrNull { it.id == id }
}

fun FragmentManager.navigate(
  @IdRes containerId: Int,
  fragment: Fragment,
  screenTransitionType: ScreenTransitionType = ScreenTransitionType.DEFAULT,
  resetBackStack: Boolean = false
) {
  commit(allowStateLoss = true) {
    if (resetBackStack) {
      fragments.forEach { remove(it) }
      setScreenTransitionType(screenTransitionType)
    } else if (findActiveFragmentById(containerId) != null) {
      setScreenTransitionType(screenTransitionType)
      addToBackStack(fragment.generateTag())
    }

    replace(containerId, fragment)
    setPrimaryNavigationFragment(fragment)
  }
}

private fun FragmentTransaction.setScreenTransitionType(
  screenTransitionType: ScreenTransitionType
) {
  when (screenTransitionType) {
    ScreenTransitionType.DEFAULT -> {
      setCustomAnimations(
        R.anim.default_fragment_entry,
        R.anim.fade_out,
        R.anim.fade_in,
        R.anim.fade_out,
      )
    }

    ScreenTransitionType.FADE -> {
      setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
    }

    ScreenTransitionType.SLIDE_FROM_BOTTOM -> {
      setCustomAnimations(
        R.anim.slide_in_from_bottom,
        R.anim.fade_out,
        R.anim.fade_in,
        R.anim.slide_out_from_bottom,
      )
    }
  }
}