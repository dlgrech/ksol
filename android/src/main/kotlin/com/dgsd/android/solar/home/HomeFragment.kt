package com.dgsd.android.solar.home

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.actionsheet.extensions.showActionSheet
import com.dgsd.android.solar.common.actionsheet.model.ActionSheetItem
import com.dgsd.android.solar.common.ui.DateTimeFormatter
import com.dgsd.android.solar.common.util.anyTrue
import com.dgsd.android.solar.extensions.ensureViewCount
import com.dgsd.android.solar.extensions.getColorAttr
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.model.TransactionInfo
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.frag_home) {

  private val viewModel: HomeViewModel by viewModel()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
    val settingsIcon = view.findViewById<View>(R.id.settings)
    val sendButton = view.findViewById<View>(R.id.send)
    val receiveButton = view.findViewById<View>(R.id.receive)
    val balanceAsAtText = view.findViewById<TextView>(R.id.balance_as_at_label)
    val balanceText = view.findViewById<TextView>(R.id.balance)
    val solLabel = view.findViewById<TextView>(R.id.sol_label)
    val transactionsContainer = view.findViewById<LinearLayout>(R.id.transactions_container)
    val shimmerBalanceText = view.findViewById<View>(R.id.shimmer_balance)
    val shimmerSolLabel = view.findViewById<View>(R.id.shimmer_sol_label)
    val shimmerBalanceAsAtText = view.findViewById<View>(R.id.shimmer_balance_as_at_label)
    val shimmerTransactionsContainer = view.findViewById<View>(R.id.shimmer_transactions_container)

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
      shimmerBalanceAsAtText.isInvisible = !it
      balanceAsAtText.isInvisible = it
      balanceText.isInvisible = it
      solLabel.isInvisible = it
    }

    onEach(viewModel.isLoadingTransactions) {
      shimmerTransactionsContainer.isInvisible = !it
      transactionsContainer.isInvisible = it
    }

    onEach(
      anyTrue(viewModel.isLoadingBalance, viewModel.isLoadingTransactions)
    ) { isLoadingData ->
      if (!isLoadingData) {
        swipeRefresh.isRefreshing = false
      }
    }

    onEach(viewModel.balanceLoadTimeText) {
      balanceAsAtText.text = it
    }

    onEach(viewModel.balanceText) {
      balanceText.text = it
    }

    onEach(viewModel.transactions) { transactions ->
      if (transactions.isNullOrEmpty()) {
        // Coming soon: Empty state
      } else {
        transactionsContainer.bindTransactions(transactions)
      }
    }

    onEach(viewModel.navigateToReceiveFlow) {
      // Coming soon..
    }

    onEach(viewModel.navigateToSettings) {
      // Coming soon..
    }

    onEach(viewModel.navigateToTransactionDetails) { transaction ->
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

  private fun LinearLayout.bindTransactions(transactions: List<TransactionInfo.FullTransaction>) {
    val layoutInflater = LayoutInflater.from(context)
    ensureViewCount(transactions.size) {
      layoutInflater.inflate(R.layout.view_transaction_content, this, true)
    }

    children.toList().zip(transactions) { view, transactionInfo ->
      val publicKeyView = view.findViewById<TextView>(R.id.public_key)
      val dateTimeView = view.findViewById<TextView>(R.id.date_time)
      val amountView = view.findViewById<TextView>(R.id.amount)
      val iconView = view.findViewById<ImageView>(R.id.icon)

      publicKeyView.text = "public key"
      amountView.text = "123.45"

      if (transactionInfo.transaction.blockTime == null) {
        dateTimeView.isVisible = false
      } else {
        dateTimeView.isVisible = true
        dateTimeView.text = DateTimeFormatter.formatRelativeDateAndTime(
          context,
          checkNotNull(transactionInfo.transaction.blockTime)
        )
      }

      if (true) {
        iconView.setImageResource(R.drawable.ic_baseline_call_made_24)
        iconView.imageTintList = ColorStateList.valueOf(
          context.getColorAttr(R.attr.colorTertiary)
        )
      } else {
        iconView.setImageResource(R.drawable.ic_baseline_call_received_24)
        iconView.imageTintList = ColorStateList.valueOf(
          context.getColorAttr(R.attr.colorError)
        )
      }

      view.setOnClickListener {
        viewModel.onTransactionClicked(transactionInfo)
      }
    }
  }

  companion object {

    fun newInstance(): HomeFragment {
      return HomeFragment()
    }
  }
}