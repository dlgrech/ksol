package com.dgsd.android.solar.repository

import com.dgsd.android.solar.common.model.Resource
import com.dgsd.ksol.model.Lamports
import kotlinx.coroutines.flow.Flow

interface SolanaApiRepository {

    fun getBalance(): Flow<Resource<Lamports>>
}