package com.dgsd.android.solar.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.model.TransactionInfo
import com.dgsd.android.solar.repository.SolanaApiRepository
import com.dgsd.ksol.model.Lamports
import kotlinx.coroutines.flow.map

private const val NUM_TRANSACTIONS_TO_DISPLAY = 5

class HomeViewModel(
    private val solanaApiRepository: SolanaApiRepository,
) : ViewModel() {

    private val balanceResourceConsumer = ResourceFlowConsumer<Lamports>(viewModelScope)
    val isLoadingBalance = balanceResourceConsumer.isLoading
    val balanceText = balanceResourceConsumer.data.map { "$it Lamports" }

    private val transactionsResourceConsumer =
        ResourceFlowConsumer<List<TransactionInfo>>(viewModelScope)
    val isLoadingTransactionSignatures = balanceResourceConsumer.isLoading
    val transactions = transactionsResourceConsumer.data

    fun onCreate() {
        balanceResourceConsumer.collectFlow(solanaApiRepository.getBalance())
        transactionsResourceConsumer.collectFlow(
            solanaApiRepository.getTransactions(NUM_TRANSACTIONS_TO_DISPLAY)
        )
    }
}