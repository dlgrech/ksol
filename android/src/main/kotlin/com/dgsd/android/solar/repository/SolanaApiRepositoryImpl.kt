package com.dgsd.android.solar.repository

import com.dgsd.android.solar.common.model.Resource
import com.dgsd.android.solar.common.util.resourceFlowOf
import com.dgsd.android.solar.session.model.WalletSession
import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.model.Lamports
import kotlinx.coroutines.flow.Flow

internal class SolanaApiRepositoryImpl(
    private val session: WalletSession,
    private val solanaApi: SolanaApi,
): SolanaApiRepository {

    override fun getBalance(): Flow<Resource<Lamports>> {
        return resourceFlowOf {
            solanaApi.getBalance(session.publicKey)
        }
    }
}