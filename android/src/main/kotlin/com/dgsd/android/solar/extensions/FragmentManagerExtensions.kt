package com.dgsd.android.solar.extensions

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.dgsd.android.solar.R

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
) {
    commit(allowStateLoss = true) {
        if (findActiveFragmentById(containerId) != null) {
            setCustomAnimations(
                R.anim.default_fragment_entry,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out,
            )

            addToBackStack(null)
        }

        replace(containerId, fragment)
        setPrimaryNavigationFragment(fragment)
    }
}