package com.dgsd.android.solar.transaction.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.cache.CacheStrategy
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.common.model.Resource
import com.dgsd.android.solar.common.ui.TransactionViewStateFactory
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.model.TransactionOrSignature
import com.dgsd.android.solar.repository.SolanaApiRepository
import com.dgsd.ksol.model.TransactionSignature
import kotlinx.coroutines.flow.map

private const val PAGE_SIZE = 10

class TransactionListViewModel(
  private val errorMessageFactory: ErrorMessageFactory,
  private val transactionViewStateFactory: TransactionViewStateFactory,
  private val solanaApiRepository: SolanaApiRepository,
) : ViewModel() {

  private val transactionsResourceConsumer =
    ResourceFlowConsumer<List<Resource<TransactionOrSignature>>>(viewModelScope)

  val isLoadingTransactions = transactionsResourceConsumer.isLoading

  val transactions = transactionsResourceConsumer.data.map { transactionsWithState ->
    transactionsWithState?.map { transactionResource ->
      transactionViewStateFactory.createForList(transactionResource)
    }
  }

  private val _navigateToTransaction = MutableEventFlow<TransactionSignature>()
  val navigateToTransaction = _navigateToTransaction.asEventFlow()

  private var hasBeenCreated = false

  fun onCreate() {
    reloadData(
      if (hasBeenCreated) {
        CacheStrategy.CACHE_IF_PRESENT
      } else {
        CacheStrategy.CACHE_AND_NETWORK
      }
    )

    hasBeenCreated = true
  }

  fun onSwipeToRefresh() {
    reloadData(CacheStrategy.NETWORK_ONLY)
  }

  private fun reloadData(cacheStrategy: CacheStrategy) {
    transactionsResourceConsumer.collectFlow(
      solanaApiRepository.getTransactions(
        cacheStrategy,
        limit = transactionsResourceConsumer.data.value?.size ?: PAGE_SIZE,
      )
    )
  }

  fun onTransactionClicked(transaction: TransactionSignature) {
    _navigateToTransaction.tryEmit(transaction)
  }

  fun onScrolledToEnd() {
    loadNextPage(CacheStrategy.CACHE_IF_PRESENT)
  }

  private fun loadNextPage(cacheStrategy: CacheStrategy) {
    // Coming soon!
  }
}