package com.dgsd.ksol.jsonrpc.networking

internal class RpcException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)