package com.dgsd.android.solar.common.modalsheet

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
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

  override fun onDestroyView() {
    modalInfo = null
    super.onDestroyView()
  }

  override fun onDismiss(dialog: DialogInterface) {
    modalInfo?.onDismiss?.invoke()
    super.onDismiss(dialog)
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

    positiveButton.updateLayoutParams<ConstraintLayout.LayoutParams> {
      if (negativeButton.isVisible) {
        startToEnd = R.id.button_horizontal_guideline
        startToStart = ConstraintSet.UNSET
        horizontalBias = 0.25f
      } else {
        startToEnd = ConstraintSet.UNSET
        startToStart = ConstraintSet.PARENT_ID
        horizontalBias = 0.5f
      }
    }
  }
}