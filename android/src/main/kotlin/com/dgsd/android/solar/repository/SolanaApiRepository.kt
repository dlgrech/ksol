package com.dgsd.android.solar.repository

import com.dgsd.android.solar.common.model.Resource
import com.dgsd.android.solar.model.TransactionInfo
import com.dgsd.ksol.model.Lamports
import kotlinx.coroutines.flow.Flow

interface SolanaApiRepository {

    fun getBalance(): Flow<Resource<Lamports>>

    fun getTransactions(limit: Int): Flow<Resource<List<TransactionInfo>>>
}