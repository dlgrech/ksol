package com.dgsd.ksol.factory

import com.dgsd.ksol.core.model.Commitment
import com.dgsd.ksol.jsonrpc.types.CommitmentConfigRequestBody

internal object CommitmentFactory {

  fun fromRpcValue(rpcValue: String?): Commitment? {
    return when (rpcValue) {
      CommitmentConfigRequestBody.FINALIZED -> Commitment.FINALIZED
      CommitmentConfigRequestBody.CONFIRMED -> Commitment.CONFIRMED
      CommitmentConfigRequestBody.PROCESSED -> Commitment.PROCESSED
      else -> null
    }
  }

  fun toRpcValue(commitment: Commitment): String {
    return when (commitment) {
      Commitment.FINALIZED -> CommitmentConfigRequestBody.FINALIZED
      Commitment.CONFIRMED -> CommitmentConfigRequestBody.CONFIRMED
      Commitment.PROCESSED -> CommitmentConfigRequestBody.PROCESSED
    }
  }
}