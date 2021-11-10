package com.dgsd.ksol.jsonrpc

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RpcRequestFactoryTests {

    @Test
    fun create_returnsExpectedResult() {
        val methodName = "sendTransaction"
        val params = arrayOf("a", "b", "c", 1, 2, 3)

        val request = RpcRequestFactory.create(methodName, *params)

        Assertions.assertEquals(SolanaJsonRpcConstants.VERSION, request.jsonRpc)
        Assertions.assertEquals(methodName, request.methodName)
        Assertions.assertEquals(params.toList(), request.params)
    }

}