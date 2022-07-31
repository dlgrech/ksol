package com.dgsd.ksol.solpay.model

sealed interface SolPayUrlParsingResult {

  data class TransferRequest(
    val request: SolPayTransferRequest
  ) : SolPayUrlParsingResult

  data class TransactionRequest(
    val request: SolPayTransactionRequest
  ) : SolPayUrlParsingResult

  data class ParsingError(
    val error: Throwable
  ) : SolPayUrlParsingResult
}