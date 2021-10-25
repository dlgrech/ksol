package com.dgsd.ksol.jsonrpc.networking

class RpcError(
    val code: Int,
    val errorMessage: String?
) : RuntimeException(errorMessage)