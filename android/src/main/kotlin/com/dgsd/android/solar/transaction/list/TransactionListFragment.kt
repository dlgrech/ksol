package com.dgsd.android.solar.transaction.list

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dgsd.android.solar.AppCoordinator
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.modalsheet.extensions.showModalFromErrorMessage
import com.dgsd.android.solar.di.util.activityViewModel
import com.dgsd.android.solar.extensions.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransactionListFragment : Fragment(R.layout.frag_transaction_list) {

  private val appCoordinator: AppCoordinator by activityViewModel()
  private val viewModel: TransactionListViewModel by viewModel()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.requireViewById<Toolbar>(R.id.toolbar)
    val swipeRefresh = view.requireViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
    val transactionList = view.requireViewById<RecyclerView>(R.id.transaction_list)

    toolbar.setNavigationOnClickListener {
      requireActivity().onBackPressed()
    }

    swipeRefresh.setOnRefreshListener {
      viewModel.onSwipeToRefresh()
    }

    val adapter = TransactionListAdapter(
      onTransactionClickedListener = { viewModel.onTransactionClicked(it) },
      onLoadMoreClickedListener = { viewModel.loadNextPage() }
    )

    transactionList.layoutManager = LinearLayoutManager(context)
    transactionList.adapter = adapter

    onEach(viewModel.isLoadingTransactions) { isLoadingTransactions ->
      if (!isLoadingTransactions) {
        swipeRefresh.isRefreshing = false
      }
    }

    onEach(viewModel.transactions) {
      adapter.transactionItems = it
    }

    onEach(viewModel.isLoadMoreItemsEnabled) {
      adapter.isLoadMoreEnabled = it
    }

    onEach(viewModel.isLoadMoreItemsLoading) {
      adapter.isLoadMoreLoading = it
    }

    onEach(viewModel.navigateToTransaction) { transactionSignature ->
      appCoordinator.navigateToTransactionDetails(transactionSignature)
    }

    onEach(viewModel.showError) {
      showModalFromErrorMessage(it)
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.onCreate()
    }
  }

  companion object {

    fun newInstance(): TransactionListFragment {
      return TransactionListFragment()
    }
  }
}