package com.dgsd.ksol.jsonrpc

import com.dgsd.ksol.jsonrpc.types.RpcRequest
import java.util.*

internal object RpcRequestFactory {

    fun create(
        methodName: String,
        vararg params: Any,
    ): RpcRequest {
        return RpcRequest(
            id = UUID.randomUUID().toString(),
            jsonRpc = SolanaJsonRpcConstants.VERSION,
            methodName = methodName,
            params = params.toList()
        )
    }
}