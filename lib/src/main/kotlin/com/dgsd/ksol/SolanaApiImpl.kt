package com.dgsd.ksol

import com.dgsd.ksol.model.Cluster
import com.dgsd.ksol.model.Commitment

/**
 * Internal implementation of [SolanaApi] interface
 */
internal class SolanaApiImpl(
    private val cluster: Cluster
) : SolanaApi {

    override suspend fun getRecentBlockhash(commitment: Commitment): String {
        TODO("Not yet implemented")
    }
}