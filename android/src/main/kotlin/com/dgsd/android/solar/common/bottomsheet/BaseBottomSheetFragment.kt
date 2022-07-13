package com.dgsd.android.solar.common.bottomsheet

import android.os.Bundle
import android.view.View
import com.dgsd.android.solar.extensions.enableBackgroundBlur
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class BaseBottomSheetFragment : BottomSheetDialogFragment() {

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    dialog?.window?.enableBackgroundBlur()
  }
}