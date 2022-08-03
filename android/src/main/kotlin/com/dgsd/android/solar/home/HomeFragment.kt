package com.dgsd.android.solar.home

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
import com.dgsd.android.solar.AppCoordinator
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.actionsheet.extensions.showActionSheet
import com.dgsd.android.solar.common.actionsheet.model.ActionSheetItem
import com.dgsd.android.solar.common.util.anyTrue
import com.dgsd.android.solar.di.util.activityViewModel
import com.dgsd.android.solar.extensions.ensureViewCount
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.model.TransactionViewState
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.frag_home) {

  private val appCoordinator: AppCoordinator by activityViewModel()
  private val viewModel: HomeViewModel by viewModel()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val swipeRefresh = view.requireViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
    val settingsIcon = view.requireViewById<View>(R.id.settings)
    val sendButton = view.requireViewById<View>(R.id.send)
    val receiveButton = view.requireViewById<View>(R.id.receive)
    val balanceAsAtText = view.requireViewById<TextView>(R.id.balance_as_at_label)
    val balanceText = view.requireViewById<TextView>(R.id.balance)
    val solLabel = view.requireViewById<TextView>(R.id.sol_label)
    val transactionsContainer = view.requireViewById<LinearLayout>(R.id.transactions_container)
    val transactionErrorContainer = view.requireViewById<View>(R.id.transaction_error_container)
    val transactionsErrorMessage = view.requireViewById<TextView>(R.id.transaction_error_message)
    val viewMoreTransactionsButton = view.requireViewById<View>(R.id.view_more_transactions)
    val shimmerBalanceText = view.requireViewById<View>(R.id.shimmer_balance)
    val shimmerSolLabel = view.requireViewById<View>(R.id.shimmer_sol_label)
    val shimmerBalanceAsAtText = view.requireViewById<View>(R.id.shimmer_balance_as_at_label)
    val shimmerTransactionsContainer = view.requireViewById<View>(R.id.shimmer_transactions_container)

    settingsIcon.setOnClickListener {
      viewModel.onSettingsClicked()
    }

    sendButton.setOnClickListener {
      viewModel.onSendButtonClicked()
    }

    receiveButton.setOnClickListener {
      viewModel.onReceiveButtonClicked()
    }

    viewMoreTransactionsButton.setOnClickListener {
      viewModel.onViewMoreTransactionsClicked()
    }

    swipeRefresh.setOnRefreshListener {
      viewModel.onSwipeToRefresh()
    }

    arrayOf(balanceText, balanceAsAtText, solLabel).forEach { v ->
      v.setOnClickListener {
        viewModel.onBalanceLabelClicked()
      }
    }

    transactionsErrorMessage.setOnClickListener {
      viewModel.onRetryTransactionLoadClicked()
    }

    onEach(viewModel.isLoadingBalance) {
      shimmerBalanceText.isInvisible = !it
      shimmerSolLabel.isInvisible = !it
      shimmerBalanceAsAtText.isInvisible = !it
      balanceAsAtText.isInvisible = it
      balanceText.isInvisible = it
      solLabel.isInvisible = it
    }

    combine(
      viewModel.isLoadingTransactions,
      viewModel.transactionsError
    ) { isLoading, errorMessage ->
      isLoading to errorMessage
    }.onEach { (isLoading, errorMessage) ->
      transactionsErrorMessage.text = errorMessage

      when {
        errorMessage != null -> {
          transactionErrorContainer.isVisible = true
          shimmerTransactionsContainer.isVisible = false
          viewMoreTransactionsButton.isVisible = false
          transactionsContainer.isVisible = false
        }

        isLoading -> {
          transactionErrorContainer.isVisible = false
          shimmerTransactionsContainer.isVisible = true
          viewMoreTransactionsButton.isVisible = false
          transactionsContainer.isVisible = false
        }

        else -> {
          transactionErrorContainer.isVisible = false
          shimmerTransactionsContainer.isVisible = false
          viewMoreTransactionsButton.isVisible = true
          transactionsContainer.isVisible = true
        }
      }

    }.launchIn(viewLifecycleOwner.lifecycleScope)

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

    onEach(viewModel.navigateToShareAddress) {
      appCoordinator.navigateToShareWalletAddress()
    }

    onEach(viewModel.navigateToTransactionsList) {
      appCoordinator.navigateToTransactionList()
    }

    onEach(viewModel.navigateToSettings) {
      appCoordinator.navigateToSettings()
    }

    onEach(viewModel.navigateToTransactionDetails) { transaction ->
      appCoordinator.navigateToTransactionDetails(transaction)
    }

    onEach(viewModel.navigateToRequestAmountFlow) {
      appCoordinator.navigateToRequestAmount()
    }

    onEach(viewModel.navigateToSendWithAddress) {
      appCoordinator.navigateToSendWithAddress()
    }

    onEach(viewModel.navigateToSendWithHistoricalAddress) {
      appCoordinator.navigateToSendWithHistoricalAddress()
    }

    onEach(viewModel.navigateToSendWithNearby) {
      appCoordinator.navigateToSendWithNearby()
    }

    onEach(viewModel.navigateToScanQr) {
      appCoordinator.navigateToSendWithQrCode()
    }

    onEach(viewModel.navigateToSendWithSolPayRequest) { requestUrl ->
      appCoordinator.navigateToSendWithSolPayRequest(requestUrl)
    }

    onEach(viewModel.showSendActionSheet) { items ->
      showSendActionSheet(items)
    }

    onEach(viewModel.showReceiveActionSheet) { items ->
      showReceiveActionSheet(items)
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


  private fun showReceiveActionSheet(items: List<ReceiveActionSheetItem>) {
    showActionSheet(
      getString(R.string.home_receive_sheet_title),
      *items.map { receiveActionSheetItem ->
        ActionSheetItem(
          title = receiveActionSheetItem.displayText,
          icon = requireContext().getDrawable(receiveActionSheetItem.iconRes),
        ) {
          viewModel.onReceiveActionSheetItemClicked(receiveActionSheetItem.type)
        }
      }.toTypedArray()
    )
  }

  private fun LinearLayout.bindTransactions(transactions: List<TransactionViewState>) {
    val layoutInflater = LayoutInflater.from(context)
    ensureViewCount(transactions.size) {
      layoutInflater.inflate(R.layout.view_transaction, this, true)
    }

    children.toList().zip(transactions) { view, transactionViewState ->
      when (transactionViewState) {
        is TransactionViewState.Error -> bindError(view, transactionViewState)
        is TransactionViewState.Loading -> bindLoading(view)
        is TransactionViewState.Transaction -> bindTransaction(view, transactionViewState)
      }
    }
  }

  private fun bindLoading(view: View) {
    view.requireViewById<View>(R.id.loading).isInvisible = false
    view.requireViewById<View>(R.id.content).isInvisible = true
    view.requireViewById<View>(R.id.error).isInvisible = true

    view.setOnClickListener(null)
  }

  private fun bindError(view: View, viewState: TransactionViewState.Error) {
    view.requireViewById<View>(R.id.loading).isInvisible = true
    view.requireViewById<View>(R.id.content).isInvisible = true
    view.requireViewById<View>(R.id.error).isInvisible = false

    if (viewState.transactionSignature == null) {
      view.setOnClickListener(null)
    } else {
      view.setOnClickListener {
        viewModel.onTransactionClicked(viewState.transactionSignature)
      }
    }
  }

  private fun bindTransaction(view: View, transaction: TransactionViewState.Transaction) {
    val contentView = view.requireViewById<View>(R.id.content)

    view.requireViewById<View>(R.id.loading).isInvisible = true
    contentView.isInvisible = false
    view.requireViewById<View>(R.id.error).isInvisible = true

    val publicKeyView = contentView.findViewById<TextView>(R.id.public_key)
    val dateTimeView = contentView.findViewById<TextView>(R.id.date_time)
    val amountView = contentView.findViewById<TextView>(R.id.amount)
    val iconView = contentView.findViewById<ImageView>(R.id.icon)

    publicKeyView.text = transaction.displayAccountText
    amountView.text = transaction.amountText

    if (transaction.dateText == null) {
      dateTimeView.isVisible = false
    } else {
      dateTimeView.isVisible = true
      dateTimeView.text = transaction.dateText
    }

    when (transaction.direction) {
      TransactionViewState.Transaction.Direction.INCOMING -> {
        iconView.setImageResource(R.drawable.ic_baseline_chevron_left_24)
      }
      TransactionViewState.Transaction.Direction.OUTGOING -> {
        iconView.setImageResource(R.drawable.ic_baseline_chevron_right_24)
      }
      TransactionViewState.Transaction.Direction.NONE -> {
        iconView.setImageResource(R.drawable.ic_baseline_commit_24)
      }
    }

    view.setOnClickListener {
      viewModel.onTransactionClicked(transaction.transactionSignature)
    }
  }

  companion object {

    fun newInstance(): HomeFragment {
      return HomeFragment()
    }
  }
}