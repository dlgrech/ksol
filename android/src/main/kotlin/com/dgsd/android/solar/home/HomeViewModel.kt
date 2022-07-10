package com.dgsd.android.solar.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.session.model.WalletSession
import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.model.Lamports
import kotlinx.coroutines.flow.map

class HomeViewModel(
    private val solanaApi: SolanaApi,
    private val currentSession: WalletSession,
) : ViewModel() {

    private val balanceResourceConsumer = ResourceFlowConsumer<Lamports>(viewModelScope)

    val isLoading = balanceResourceConsumer.isLoading

    val balanceText = balanceResourceConsumer.data.map { "$it Lamports" }

    fun onCreate() {
        balanceResourceConsumer.collectFlow {
            solanaApi.getBalance(currentSession.publicKey)
        }
    }
}