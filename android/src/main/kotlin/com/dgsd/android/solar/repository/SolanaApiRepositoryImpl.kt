package com.dgsd.android.solar.repository

import com.dgsd.android.solar.common.model.Resource
import com.dgsd.android.solar.common.util.resourceFlowOf
import com.dgsd.android.solar.model.TransactionInfo
import com.dgsd.android.solar.session.model.WalletSession
import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.model.Lamports
import com.dgsd.ksol.model.TransactionSignature
import kotlinx.coroutines.flow.Flow

internal class SolanaApiRepositoryImpl(
    private val session: WalletSession,
    private val solanaApi: SolanaApi,
) : SolanaApiRepository {

    override fun getBalance(): Flow<Resource<Lamports>> {
        return resourceFlowOf {
            solanaApi.getBalance(session.publicKey)
        }
    }

    override fun getTransactions(limit: Int): Flow<Resource<List<TransactionInfo>>> {
        return resourceFlowOf {
            solanaApi.getSignaturesForAddress(
                accountKey = session.publicKey,
                limit = limit
            ).map {
                getTransaction(it.signature)
            }
        }
    }

    private suspend fun getTransaction(signature: TransactionSignature): TransactionInfo {
        val transaction = solanaApi.getTransaction(signature)
        return if (transaction == null) {
            TransactionInfo.UnknownTransaction(signature)
        } else {
            TransactionInfo.FullTransaction(transaction)
        }
    }
}