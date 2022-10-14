package com.dgsd.android.solar.di.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel

inline fun <reified T : ViewModel> Fragment.parentViewModel(): Lazy<T> {
  return viewModels(ownerProducer = { requireParentFragment() })
}

inline fun <reified T : ViewModel> Fragment.activityViewModel(): Lazy<T> {
  return viewModels(ownerProducer = { requireActivity() })
}