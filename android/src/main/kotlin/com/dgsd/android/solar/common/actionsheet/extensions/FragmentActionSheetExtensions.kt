package com.dgsd.android.solar.common.actionsheet.extensions

import androidx.fragment.app.Fragment
import com.dgsd.android.solar.common.actionsheet.ActionSheetFragment
import com.dgsd.android.solar.common.actionsheet.model.ActionSheetItem

fun Fragment.showActionSheet(vararg items: ActionSheetItem) {
  val fragment = ActionSheetFragment()
  fragment.actionSheetItems = items.toList()
  fragment.show(childFragmentManager, null)
}