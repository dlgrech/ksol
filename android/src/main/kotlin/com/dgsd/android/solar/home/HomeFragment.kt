package com.dgsd.android.solar.home

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.actionsheet.extensions.showActionSheet
import com.dgsd.android.solar.common.actionsheet.model.ActionSheetItem
import com.dgsd.android.solar.extensions.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.frag_home) {

  private val viewModel: HomeViewModel by viewModel()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
    val settingsIcon = view.findViewById<View>(R.id.settings)
    val sendButton = view.findViewById<View>(R.id.send)
    val receiveButton = view.findViewById<View>(R.id.receive)
    val balanceText = view.findViewById<TextView>(R.id.balance)
    val solLabel = view.findViewById<TextView>(R.id.sol_label)
    val shimmerBalanceText = view.findViewById<View>(R.id.shimmer_balance)
    val shimmerSolLabel = view.findViewById<View>(R.id.shimmer_sol_label)

    settingsIcon.setOnClickListener {
      viewModel.onSettingsClicked()
    }

    sendButton.setOnClickListener {
      viewModel.onSendButtonClicked()
    }

    receiveButton.setOnClickListener {
      viewModel.onReceiveButtonClicked()
    }

    swipeRefresh.setOnRefreshListener {
      viewModel.onSwipeToRefresh()
    }

    onEach(viewModel.isLoadingBalance) {
      shimmerBalanceText.isInvisible = !it
      shimmerSolLabel.isInvisible = !it
      balanceText.isInvisible = it
      solLabel.isInvisible = it

      if (!it) {
        swipeRefresh.isRefreshing = false
      }
    }

    onEach(viewModel.balanceText) {
      balanceText.text = it
    }

    onEach(viewModel.navigateToReceiveFlow) {
      // Coming soon
    }

    onEach(viewModel.navigateToSettings) {
      // Coming soon..
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