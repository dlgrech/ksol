package com.dgsd.android.solar.transaction.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.cache.CacheStrategy
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.common.ui.SolTokenFormatter
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.extensions.getString
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.repository.SolanaApiRepository
import com.dgsd.ksol.model.Transaction
import com.dgsd.ksol.model.TransactionSignature
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class TransactionDetailsViewModel(
  application: Application,
  private val solanaApiRepository: SolanaApiRepository,
  private val errorMessageFactory: ErrorMessageFactory,
  private val transactionSignature: TransactionSignature,
) : AndroidViewModel(application) {

  private val transactionResourceConsumer = ResourceFlowConsumer<Transaction>(viewModelScope)

  private val transaction = transactionResourceConsumer.data.filterNotNull()

  val isLoading = transactionResourceConsumer.isLoading

  val showLoadingState = transactionResourceConsumer.isLoadingOrError

  val transactionSignatureHeaderText = transaction.map {
    if (it.signatures.size != 1) {
      getString(R.string.transaction_details_section_title_signatures)
    } else {
      getString(R.string.transaction_details_section_title_signature)
    }
  }

  val transactionSignatures = transaction.map { it.signatures }

  val feeText = transaction.map { SolTokenFormatter.formatLong(it.metadata.fee) }

  val errorMessage = transactionResourceConsumer.error
    .filterNotNull()
    .map { errorMessageFactory.create(it) }
    .asEventFlow(viewModelScope)

  fun onCreate() {
    reloadData(CacheStrategy.CACHE_IF_PRESENT)
  }

  fun onSwipeToRefresh() {
    reloadData(CacheStrategy.NETWORK_ONLY)
  }

  private fun reloadData(cacheStrategy: CacheStrategy) {
    transactionResourceConsumer.collectFlow(
      solanaApiRepository.getTransaction(
        cacheStrategy,
        transactionSignature
      )
    )
  }
}