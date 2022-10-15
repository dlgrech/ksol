package com.dgsd.ksol.jsonrpc.networking

internal class RpcError(
  val code: Int,
  val errorMessage: String?
) : RuntimeException(errorMessage)