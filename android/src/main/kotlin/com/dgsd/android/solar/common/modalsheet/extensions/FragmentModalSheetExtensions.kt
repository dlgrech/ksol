package com.dgsd.android.solar.common.modalsheet.extensions

import androidx.fragment.app.Fragment
import com.dgsd.android.solar.common.modalsheet.ModalSheetFragment
import com.dgsd.android.solar.common.modalsheet.model.ModalInfo

fun Fragment.showModal(modalInfo: ModalInfo) {
  val fragment = ModalSheetFragment()
  fragment.modalInfo = modalInfo
  fragment.show(childFragmentManager, null)
}