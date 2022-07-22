package com.dgsd.android.solar.home

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.actionsheet.extensions.showActionSheet
import com.dgsd.android.solar.common.actionsheet.model.ActionSheetItem
import com.dgsd.android.solar.extensions.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.frag_home) {

  private val viewModel: HomeViewModel by viewModel()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val sendButton = view.findViewById<View>(R.id.send)
    val receiveButton = view.findViewById<View>(R.id.receive)
    val balanceText = view.findViewById<TextView>(R.id.balance)

    sendButton.setOnClickListener {
      viewModel.onSendButtonClicked()
    }

    receiveButton.setOnClickListener {
      viewModel.onReceiveButtonClicked()
    }

    onEach(viewModel.balanceText) {
      balanceText.text = it
    }

    onEach(viewModel.navigateToReceiveFlow) {
      // Coming soon
    }

    onEach(viewModel.showSendActionSheet) { items ->
      showSendActionSheet(items)
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.onCreate()
    }
  }

  private fun showSendActionSheet(items: List<SendActionSheetItem>) {
    showActionSheet(
      getString(R.string.home_send_sheet_title),
      *items.map { sendActionSheetItem ->
        ActionSheetItem(
          title = sendActionSheetItem.displayText,
          icon = requireContext().getDrawable(sendActionSheetItem.iconRes),
        ) {
          viewModel.onSendActionSheetItemClicked(sendActionSheetItem.type)
        }
      }.toTypedArray()
    )
  }

  companion object {

    fun newInstance(): HomeFragment {
      return HomeFragment()
    }
  }
}