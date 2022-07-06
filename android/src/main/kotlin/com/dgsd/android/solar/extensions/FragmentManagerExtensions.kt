package com.dgsd.android.solar.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * Similar to [FragmentManager.findFragmentById], but ensures the fragment found is actually visible
 */
fun FragmentManager.findActiveFragmentById(id: Int): Fragment? {
    val visibleFragment = this.fragments.firstOrNull {
        it.id == id && it.isVisible
    }

    return visibleFragment ?: this.fragments.firstOrNull { it.id == id }
}