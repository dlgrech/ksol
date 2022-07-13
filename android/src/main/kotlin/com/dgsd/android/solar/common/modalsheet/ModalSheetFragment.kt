package com.dgsd.android.solar.common.modalsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.bottomsheet.BaseBottomSheetFragment
import com.dgsd.android.solar.common.modalsheet.model.ModalInfo

class ModalSheetFragment : BaseBottomSheetFragment() {

  var modalInfo: ModalInfo? = null
    set(value) {
      field = value
      if (view != null) {
        rebind()
      }
    }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.frag_modal_action_sheet, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    rebind()
  }

  private fun rebind() {
    val titleView = requireView().findViewById<TextView>(R.id.title)
    val messageView = requireView().findViewById<TextView>(R.id.message)
    val positiveButton = requireView().findViewById<Button>(R.id.positive)
    val negativeButton = requireView().findViewById<TextView>(R.id.negative)

    titleView.text = modalInfo?.title
    titleView.isVisible = !modalInfo?.title.isNullOrEmpty()

    messageView.text = modalInfo?.message
    messageView.isVisible = !modalInfo?.message.isNullOrEmpty()

    positiveButton.text = modalInfo?.positiveButton?.text
    positiveButton.setOnClickListener {
      modalInfo?.positiveButton?.onClick?.invoke()
      dismissAllowingStateLoss()
    }

    negativeButton.text = modalInfo?.negativeButton?.text
    negativeButton.isVisible = !modalInfo?.negativeButton?.text.isNullOrEmpty()
    negativeButton.setOnClickListener {
      modalInfo?.negativeButton?.onClick?.invoke()
      dismissAllowingStateLoss()
    }
  }
}