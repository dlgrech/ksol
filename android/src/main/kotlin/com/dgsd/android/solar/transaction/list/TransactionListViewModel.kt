package com.dgsd.android.solar.transaction.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.cache.CacheStrategy
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.common.model.Resource
import com.dgsd.android.solar.common.ui.TransactionViewStateFactory
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.model.TransactionOrSignature
import com.dgsd.android.solar.repository.SolanaApiRepository
import com.dgsd.ksol.model.TransactionSignature
import kotlinx.coroutines.flow.*

class TransactionListViewModel(
  private val errorMessageFactory: ErrorMessageFactory,
  private val transactionViewStateFactory: TransactionViewStateFactory,
  private val solanaApiRepository: SolanaApiRepository,
) : ViewModel() {

  private val transactionsResourceConsumer =
    ResourceFlowConsumer<List<Resource<TransactionOrSignature>>>(viewModelScope)

  val isLoadingTransactions = transactionsResourceConsumer.isLoadingOrError

  private val _showError = MutableEventFlow<CharSequence>()
  val showError = _showError.asEventFlow()

  private val lastBeforeTransaction = MutableStateFlow<TransactionSignature?>(null)

  private val _isLoadMoreItemsEnabled = MutableStateFlow(true)
  val isLoadMoreItemsEnabled = _isLoadMoreItemsEnabled.asStateFlow()

  val isLoadMoreItemsLoading = combine(
    lastBeforeTransaction,
    transactionsResourceConsumer.isLoading,
  ) { lastBeforeTransaction, isLoading ->
    lastBeforeTransaction != null && isLoading
  }

  private val _transactions = MutableStateFlow(emptyList<Resource<TransactionOrSignature>>())
  val transactions = _transactions.map { transactionsWithState ->
    transactionsWithState.map { transactionResource ->
      transactionViewStateFactory.createForList(transactionResource)
    }
  }

  private val _navigateToTransaction = MutableEventFlow<TransactionSignature>()
  val navigateToTransaction = _navigateToTransaction.asEventFlow()

  private var hasBeenCreated = false

  fun onCreate() {
    onEach(transactionsResourceConsumer.data.filterNotNull()) { newTransactions ->
      val current = _transactions.value
      _transactions.value = merge(current, newTransactions)
    }

    onEach(transactionsResourceConsumer.error.filterNotNull()) {
      _showError.tryEmit(errorMessageFactory.create(it))
    }

    reloadData(
      if (hasBeenCreated) {
        CacheStrategy.CACHE_IF_PRESENT
      } else {
        CacheStrategy.CACHE_AND_NETWORK
      },
      beforeTransaction = null
    )

    hasBeenCreated = true
  }

  fun onSwipeToRefresh() {
    reloadData(CacheStrategy.NETWORK_ONLY, beforeTransaction = null)
  }

  private fun reloadData(
    cacheStrategy: CacheStrategy,
    beforeTransaction: TransactionSignature?
  ) {
    lastBeforeTransaction.value = beforeTransaction
    transactionsResourceConsumer.collectFlow(
      solanaApiRepository.getTransactions(cacheStrategy, beforeTransaction)
    )
  }

  fun onTransactionClicked(transaction: TransactionSignature) {
    _navigateToTransaction.tryEmit(transaction)
  }

  fun loadNextPage() {
    reloadData(
      cacheStrategy = CacheStrategy.CACHE_IF_PRESENT,
      beforeTransaction = _transactions.value.lastOrNull()?.dataOrNull()?.signature()
    )
  }

  private fun merge(
    current: List<Resource<TransactionOrSignature>>,
    incoming: List<Resource<TransactionOrSignature>>
  ): List<Resource<TransactionOrSignature>> {
    val incomingSignatures = incoming.mapNotNull { it.dataOrNull()?.signature() }.toSet()
    val result = current.filter {
      val currentTransactionOrSignature = it.dataOrNull()
      currentTransactionOrSignature == null ||
        currentTransactionOrSignature.signature() !in incomingSignatures
    }.toMutableList()

    if (lastBeforeTransaction.value == null) {
      result.addAll(0, incoming)
    } else if (incoming.isEmpty()) {
      _isLoadMoreItemsEnabled.value = false
    } else {
      result.addAll(incoming)
    }

    return result.sortedByDescending {
      when (it) {
        is Resource.Error -> 0
        is Resource.Loading -> 0
        is Resource.Success -> {
          it.data.transactionOrThrow().blockTime?.toEpochSecond() ?: 0
        }
      }
    }
  }
}